package com.mygdx.game;


import static com.mygdx.game.HealthHaven.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;

public class ScreenGame implements Screen {
    HealthHaven s; // ссылка на объект главного класса игры
    boolean isAccelerometerPresent; // флаг наличия акселерометра в телефоне

    // текстуры
    Texture imgBG;
    Texture imgShip;
    Texture imgEnemy;
    //Texture imgShot;
    Texture imgAtlasFragments;
    TextureRegion[][] imgFragment = new TextureRegion[2][4];
    // звуки
    //Sound sndShoot;
    //Sound sndExplosion;

    // кнопки
    SpaceButton btnExit;

    // игровые объекты
    ArrayList<Item> enemy = new ArrayList<>();
    //ArrayList<ShipShot> shots = new ArrayList<>();
    ArrayList<Fragment> fragments = new ArrayList<>();
    Guy ship;
    Player[] players = new Player[6]; // игроки в таблице рекордов

    // переменные для работы со таймером
    long timeEnemySpawn, timeEnemyInterval;
    long timeShipDestroy, timeShipAliveInterval = 6000;

    int kills; // количество сбитых кораблей
    boolean isGameOver; // флаг окончания игры

    public ScreenGame(HealthHaven healthHaven) {
        s = healthHaven;
        // проверяем наличие акселерометра в устройстве
        isAccelerometerPresent = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

        // загружаем изображения
        imgBG = new Texture("bg_game.png");
        imgShip = new Texture("ship.png");
        imgEnemy = new Texture("enemy.png");
        //imgShot = new Texture("shipshot.png");
        imgAtlasFragments = new Texture("atlasfragment.png");
        // режем атлас изображений на текстуры
        for (int i = 0; i < imgFragment[0].length; i++) {
            imgFragment[0][i] = new TextureRegion(imgAtlasFragments, i*200, 0, 200, 200);
            imgFragment[1][i] = new TextureRegion(imgAtlasFragments, i*200, 200, 200, 200);
        }
        // загружаем звуки
        //sndShoot = Gdx.audio.newSound(Gdx.files.internal("blaster.mp3"));
        //sndExplosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

        // создаём кнопки
        btnExit = new SpaceButton(s.fontSmall, "exit", SCR_WIDTH-100, 20);

        // создаём и загружаем таблицу рекордов
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player("Noname", 0);
        }
        loadTableOfRecords();

        // создаём объекты звёздного неба
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
            ship.vx = (s.touch.x - ship.x)/50;
            //ship.vy = (s.touch.y - ship.y)/50;
            if(btnExit.hit(s.touch.x, s.touch.y)) {
                s.setScreen(s.screenIntro);
            }
        } else if(isAccelerometerPresent) { // проверяем наклон акселерометра
            ship.vx = -Gdx.input.getAccelerometerX()*2;
        }

        // +++++++++++++++ события игры ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // небо

        // вражеские корабли
        if(ship.isAlive){
            spawnEnemy();
        }
        for (int i = 0; i < enemy.size(); i++) {
            enemy.get(i).move();
            if(enemy.get(i).outOfScreen()) {
                if(ship.isAlive){
                    destroyShip();
                }
                enemy.remove(i);
                i--;
            }
        }

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
        // обломки
        for (int i = 0; i < fragments.size(); i++) {
            fragments.get(i).move();
            if(fragments.get(i).outOfScreen()) {
                fragments.remove(i);
                i--;
            }
        }

        // наш космический корабль
        if(ship.isAlive){
            ship.move();
        } else {
            if(!isGameOver) {
                if (timeShipDestroy + timeShipAliveInterval < TimeUtils.millis()) {
                    ship.isAlive = true;
                    ship.x = SCR_WIDTH / 2;
                }
            }
        }

        // +++++++++++++++ отрисовка всего +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        s.camera.update();
        s.batch.setProjectionMatrix(s.camera.combined);
        s.batch.begin();
        s.batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        for (int i = 0; i < fragments.size(); i++) {
            s.batch.draw(imgFragment[fragments.get(i).typeShip][fragments.get(i).typeFragment],
                    fragments.get(i).getX(), fragments.get(i).getY(),
                    fragments.get(i).width/2, fragments.get(i).height/2,
                    fragments.get(i).width, fragments.get(i).height,
                    1, 1, fragments.get(i).angle);
        }
        for (int i = 0; i < enemy.size(); i++) {
            s.batch.draw(imgEnemy, enemy.get(i).getX(), enemy.get(i).getY(), enemy.get(i).width, enemy.get(i).height);
        }
        /*
        for (int i = 0; i < shots.size(); i++) {
            s.batch.draw(imgShot, shots.get(i).getX(), shots.get(i).getY(), shots.get(i).width, shots.get(i).height);
        }
        */
        if(ship.isAlive) {
            s.batch.draw(imgShip, ship.getX(), ship.getY(), ship.width, ship.height);
        }
        for (int i = 0; i < ship.lives; i++) {
            s.batch.draw(imgShip, SCR_WIDTH-40-40*i, SCR_HEIGHT-40, 30, 30);
        }
        s.fontSmall.draw(s.batch, "KILLS: "+kills, 20, SCR_HEIGHT-20);
        btnExit.font.draw(s.batch, btnExit.text, btnExit.x, btnExit.y);
        if(isGameOver){
            s.fontLarge.draw(s.batch, "GAME OVER", 0, SCR_HEIGHT/5*3, SCR_WIDTH, Align.center, false);
            for (int i = 0; i < players.length; i++) {
                String str = players[i].name + "......." + players[i].kills;
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
        imgShip.dispose();
        imgEnemy.dispose();
        //imgShot.dispose();
        imgAtlasFragments.dispose();
    }

    void spawnEnemy() {
        if(TimeUtils.millis() > timeEnemySpawn+timeEnemyInterval) {
            enemy.add(new Item(100, 100));
            timeEnemySpawn = TimeUtils.millis();
        }
    }

    void spawnShot() {
        /*
        if(TimeUtils.millis() > timeShotSpawn+timeShotInterval) {
            shots.add(new ShipShot(ship.x, ship.y, ship.width, ship.height));
            timeShotSpawn = TimeUtils.millis();
            //if(s.sound) sndShoot.play();
        }
        */
    }

    void spawnFragments(float x, float y, float shipSize, int type) {
        for (int i = 0; i < 60; i++) {
            fragments.add(new Fragment(x, y, shipSize, type));
        }
    }

    void destroyShip() {
        spawnFragments(ship.x, ship.y, ship.width, 1);
        //if(s.sound) sndExplosion.play();
        ship.isAlive = false;
        ship.lives--;
        if(ship.lives == 0) {
            gameOver();
        }
        timeShipDestroy = TimeUtils.millis();
    }

    void gameOver() {
        isGameOver = true;
        players[players.length-1].name = s.playerName;
        players[players.length-1].kills = kills;
        sortTableOfRecords();
        saveTableOfRecords();
    }

    void gameStart() {
        isGameOver = false; // выключаем флаг окончания игры
        // удаляем все объекты на экране
        enemy.clear();
        //shots.clear();
        fragments.clear();
        kills = 0; // обнуляем фраги
        ship = new Guy(SCR_WIDTH/2, 100, 100, 100); // создаём объект космического корабля
        // определяем интервал спауна врагов в зависимости от уровня игры
        if(s.modeOfGame == MODE_EASY) {
            timeEnemyInterval = 2000;
        } else if(s.modeOfGame == MODE_NORMAL) {
            timeEnemyInterval = 1200;
        } else if(s.modeOfGame == MODE_HARD) {
            timeEnemyInterval = 700;
        }
    }

    // сохраниение таблицы рекордов
    void saveTableOfRecords(){
        Preferences prefs = Gdx.app.getPreferences("Table Of Space Records");
        for (int i = 0; i < players.length; i++) {
            prefs.putString("name"+i, players[i].name);
            prefs.putInteger("kills"+i, players[i].kills);
        }
        prefs.flush(); // записываем
    }

    // считываем таблицу рекордов
    void loadTableOfRecords(){
        Preferences prefs = Gdx.app.getPreferences("Table Of Space Records");
        for (int i = 0; i < players.length; i++) {
            if(prefs.contains("name"+i)) players[i].name = prefs.getString("name"+i);
            if(prefs.contains("kills"+i)) players[i].kills = prefs.getInteger("kills"+i);
        }
    }

    // сортируем таблицу рекордов
    void sortTableOfRecords(){
        for (int j = 0; j < players.length-1; j++) {
            for (int i = 0; i < players.length-1; i++) {
                if(players[i].kills < players[i+1].kills){ // по убыванию
                    int c = players[i].kills;
                    players[i].kills = players[i+1].kills;
                    players[i+1].kills = c;
                    String s = players[i].name;
                    players[i].name = players[i+1].name;
                    players[i+1].name = s;
					/* Player c = players[i];
					players[i] = players[i+1];
					players[i+1] = c; */
                }
            }
        }
    }

    // очищаем таблицу рекордов
    void clearTableOfRecords(){
        for (int i = 0; i < players.length; i++) {
            players[i].name = "Noname";
            players[i].kills = 0;
        }
    }
}
