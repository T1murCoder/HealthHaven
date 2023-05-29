package com.t1murcoder.healthhaven;


import static com.t1murcoder.healthhaven.HealthHaven.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;

public class ScreenGame implements Screen {
    HealthHaven s; // ссылка на объект главного класса игры
    boolean isAccelerometerPresent; // флаг наличия акселерометра в телефоне

    // текстуры
    Texture imgBG;
    Texture imgGuyAtlas;
    TextureRegion[][] imgGuy = new TextureRegion[2][9];

    public static TextureRegion[] imgGoodItem = new TextureRegion[3];
    public static TextureRegion[] imgBadItem = new TextureRegion[4];
    // звуки

    // кнопки
    HavenButton btnExit;

    // игровые объекты
    ArrayList<HavenItem> items = new ArrayList<>();

    Guy guy;
    Player[] players = new Player[6]; // игроки в таблице рекордов

    TypeItem[] typeItems = new TypeItem[7];

    // время
    long timeStart, timeCurrent;

    // переменные для работы со таймером
    long timeItemSpawn, timeItemInterval;
    long timeShipDestroy, timeShipAliveInterval = 6000;
    int timeCounter = 0;

    boolean isGameOver; // флаг окончания игры
    int hpPerSecond;

    public ScreenGame(HealthHaven healthHaven) {
        s = healthHaven;
        // проверяем наличие акселерометра в устройстве
        isAccelerometerPresent = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

        // загружаем изображения
        imgBG = new Texture("bg_game.png");
        imgGuyAtlas = new Texture("man_go_atlas.png");
        for (int i = 0; i < imgGuy[0].length; i++) {
            imgGuy[0][i] = new TextureRegion(imgGuyAtlas, 256*i, 0, 256, 380);
            imgGuy[1][i] = new TextureRegion(imgGuyAtlas, 256*i, 380, 256, 380);
        }

        for (int i = 0; i < imgGoodItem.length; i++) {
            imgGoodItem[i] = new TextureRegion(new Texture("items/good/" + (i + 1) + ".png"));
        }

        for (int i = 0; i < imgBadItem.length; i++) {
            imgBadItem[i] = new TextureRegion(new Texture("items/bad/" + (i + 1) + ".png"));
        }

        typeItems[0] = new TypeItem(imgGoodItem[0], 5, "Apple");
        typeItems[1] = new TypeItem(imgGoodItem[1], 3, "Banana");
        typeItems[2] = new TypeItem(imgGoodItem[2], 10, "Greek salad");
        typeItems[3] = new TypeItem(imgBadItem[0], -7, "Beer");
        typeItems[4] = new TypeItem(imgBadItem[1], -5, "Beer can");
        typeItems[5] = new TypeItem(imgBadItem[2], -15, "Cigarette");
        typeItems[6] = new TypeItem(imgBadItem[3], -25, "Drugs");

        // загружаем звуки
        //sndShoot = Gdx.audio.newSound(Gdx.files.internal("blaster.mp3"));
        //sndExplosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

        // создаём кнопки
        btnExit = new HavenButton(s.fontSmall, "exit", SCR_WIDTH-100, 20);

        // создаём и загружаем таблицу рекордов
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player("Noname", 0);
        }
        loadTableOfRecords();
    }

    @Override // срабатывает при переходе на этот screen
    public void show() {
        gameStart();
    }

    @Override
    public void render(float delta) {
        // +++++++++++++++ касания экрана/клики мышью ++++++++++++++++++++++++++++++++++++++++++++++
        if(Gdx.input.isTouched()) {
            s.touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            s.camera.unproject(s.touch);
            guy.vx = (s.touch.x - guy.x)/50;
            if(Math.abs(guy.vx) > 10) guy.vx = guy.vx>0?10:-10;
            //ship.vy = (s.touch.y - ship.y)/50;
            if(btnExit.hit(s.touch.x, s.touch.y)) {
                s.setScreen(s.screenIntro);
            }
        } else if(isAccelerometerPresent) { // проверяем наклон акселерометра
            guy.vx = -Gdx.input.getAccelerometerX()*2;
        }

        // +++++++++++++++ события игры ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        timeCounter += 1;

        // спавн предметов
        if(guy.isAlive){
            spawnItem();
        }

        for (int i = 0; i < items.size(); i++) {
            items.get(i).move();
            if (items.get(i).outOfScreen()) {
                items.remove(i);
                i--;
                continue;
            }

            if (guy.isAlive && guy.overlap(items.get(i))) {
                changeHealth(items.get(i).type.impact);
                items.remove(i);
                i--;
            }
        }
        // TODO: Сделать звуки
        // TODO: Сделать другие фоны для настроек, about
        // наши выстрелы
        /*
        if(ship.isAlive){
            spawnShot();
        }
        */
        /*
        for (int i = 0; i < shots.size(); i++) {
            shots.get(i).move();
            if(shots.get(i).outOfScreen()) {
                shots.remove(i);
                i--;
                continue;
            }
            // попадание выстрела в вражеский корабль
            for (int j = 0; j < enemy.size(); j++) {
                if(shots.get(i).overlap(enemy.get(j))) {
                    spawnFragments(enemy.get(j).x, enemy.get(j).y, enemy.get(j).width, 0);
                    enemy.remove(j);
                    shots.remove(i);
                    kills++;
                    i--;
                    //if(s.sound) sndExplosion.play();
                    break;
                }
            }
        }
         */

        // наш космический корабль
        if(guy.isAlive){
            guy.move();
        } else {
            if(!isGameOver) {
                if (timeShipDestroy + timeShipAliveInterval < TimeUtils.millis()) {
                    guy.isAlive = true;
                    guy.x = SCR_WIDTH / 2;
                }
            }
        }

        if(!isGameOver) {
            timeCurrent = TimeUtils.millis() - timeStart;
        }

        takeHealthEverySecond(hpPerSecond);

        // +++++++++++++++ отрисовка всего +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        s.camera.update();
        s.batch.setProjectionMatrix(s.camera.combined);
        s.batch.begin();
        s.batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        /*
        for (int i = 0; i < enemy.size(); i++) {
            s.batch.draw(imgEnemy, enemy.get(i).getX(), enemy.get(i).getY(), enemy.get(i).width, enemy.get(i).height);
        }

         */

        if(guy.isAlive) {
            s.batch.draw(imgGuy[0][guy.phase], guy.getX(), guy.getY(), guy.width/2, guy.height/2,
                    guy.width, guy.height, guy.flip(), 1, 0);
        }

        for (HavenItem elem: items) {
            s.batch.draw(elem.type.img,
                    elem.getX(), elem.getY(),
                    elem.width/2, elem.height/2,
                    elem.width, elem.height,
                    1, 1, elem.angle);
        }

        /*
        for (int i = 0; i < items.size(); i++) {
            s.batch.draw(items.get(i).type.img,
                    items.get(i).getX(), items.get(i).getY(),
                    items.get(i).width/2, items.get(i).height/2,
                    items.get(i).width, items.get(i).height,
                    1, 1, items.get(i).angle);
        }
        */
        if(guy.isAlive) {
            s.batch.draw(imgGuy[1][guy.phase], guy.getX(), guy.getY(), guy.width/2, guy.height/2,
                    guy.width, guy.height, guy.flip(), 1, 0);
        }

        s.fontSmall.draw(s.batch, "TIME: "+timeToString(timeCurrent), SCR_WIDTH-250, SCR_HEIGHT-20);
        s.fontSmall.draw(s.batch, "HEALTH: "+ guy.health, 20, SCR_HEIGHT-20);
        btnExit.font.draw(s.batch, btnExit.text, btnExit.x, btnExit.y);
        if(isGameOver){
            s.fontLarge.draw(s.batch, "GAME OVER", 0, SCR_HEIGHT/5*3, SCR_WIDTH, Align.center, false);
            for (int i = 0; i < players.length; i++) {
                String str = players[i].name + "......." + timeToString(players[i].time);
                s.fontSmall.draw(s.batch, str, 0, SCR_HEIGHT/2-i*40, SCR_WIDTH, Align.center, true);
            }
        }
        s.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        imgBG.dispose();
        imgGuyAtlas.dispose();
    }

    void spawnItem() {
        if(TimeUtils.millis() > timeItemSpawn + timeItemInterval) {
            items.add(new HavenItem(75, 75, typeItems[MathUtils.random(0, typeItems.length - 1)]));
            timeItemSpawn = TimeUtils.millis();
        }
    }

    void changeHealth(int amount) {
        guy.health += amount;
        if (guy.health > 100) guy.health = 100;
        if (guy.health <= 0) {
            guy.health = 0;
            gameOver();
        }
    }

    void takeHealthEverySecond(int amount) {
        if (timeCounter % 60 == 0 && guy.isAlive) {
            changeHealth(-amount);
        }
    }

    String timeToString(long time){
        String min = "" + time/1000/60/10 + time/1000/60%10;
        String sec = "" + time/1000%60/10 + time/1000%60%10;
        return min+":"+sec;
    }


    void gameOver() {
        isGameOver = true;
        guy.isAlive = false;
        players[players.length-1].name = s.playerName;
        players[players.length-1].time = timeCurrent;
        sortTableOfRecords();
        saveTableOfRecords();
    }

    void gameStart() {
        isGameOver = false; // выключаем флаг окончания игры
        // удаляем все объекты на экране
        items.clear();
        timeStart = TimeUtils.millis(); // время старта
        guy = new Guy(SCR_WIDTH/2, 170, 256*0.8f, 380*0.8f); // создаём объект гуя
        // определяем интервал спауна врагов в зависимости от уровня игры
        if(s.modeOfGame == MODE_EASY) {
            timeItemInterval = 900;
            hpPerSecond = 1;
        } else if(s.modeOfGame == MODE_NORMAL) {
            timeItemInterval = 600;
            hpPerSecond = 2;
        } else if(s.modeOfGame == MODE_HARD) {
            timeItemInterval = 300;
            hpPerSecond = 3;
        }
    }

    // сохраниение таблицы рекордов
    void saveTableOfRecords(){
        Preferences prefs = Gdx.app.getPreferences("Table Of Space Records");
        for (int i = 0; i < players.length; i++) {
            prefs.putString("name"+i, players[i].name);
            prefs.putLong("time"+i, players[i].time);
        }
        prefs.flush(); // записываем
    }

    // считываем таблицу рекордов
    void loadTableOfRecords(){
        Preferences prefs = Gdx.app.getPreferences("Table Of Space Records");
        for (int i = 0; i < players.length; i++) {
            if(prefs.contains("name"+i)) players[i].name = prefs.getString("name"+i);
            if(prefs.contains("time"+i)) players[i].time = prefs.getLong("time"+i);
        }
    }

    // сортируем таблицу рекордов
    void sortTableOfRecords(){
        for (int j = 0; j < players.length-1; j++) {
            for (int i = 0; i < players.length-1; i++) {
                if(players[i].time < players[i+1].time){ // по убыванию
					Player c = players[i];
					players[i] = players[i+1];
					players[i+1] = c;
                }
            }
        }
    }

    // очищаем таблицу рекордов
    void clearTableOfRecords(){
        for (int i = 0; i < players.length; i++) {
            players[i].name = "Noname";
            players[i].time = 0;
        }
    }
}
