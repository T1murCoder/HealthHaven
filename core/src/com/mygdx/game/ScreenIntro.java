package com.mygdx.game;


import static com.mygdx.game.HealthHaven.SCR_HEIGHT;
import static com.mygdx.game.HealthHaven.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class ScreenIntro implements Screen {
    HealthHaven s;
    Texture imgBackGround; // фон
    SpaceButton btnGame, btnSettings, btnAbout, btnExit;

    public ScreenIntro(HealthHaven healthHaven) {
        s = healthHaven;
        imgBackGround = new Texture("space00.jpg");
        // создаём кнопки
        btnGame = new SpaceButton(s.fontLarge, "PLAY", 50, 450);
        btnSettings = new SpaceButton(s.fontLarge, "SETTINGS", 50, 350);
        btnAbout = new SpaceButton(s.fontLarge, "ABOUT", 50, 250);
        btnExit = new SpaceButton(s.fontLarge, "EXIT", 50, 150);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // касания экрана/клики мышью
        if(Gdx.input.justTouched()) {
            s.touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            s.camera.unproject(s.touch);
            if(btnGame.hit(s.touch.x, s.touch.y)){
                sleep(100);
                s.setScreen(s.screenGame);
            }
            if(btnSettings.hit(s.touch.x, s.touch.y)){
                s.setScreen(s.screenSettings);
            }
            if(btnAbout.hit(s.touch.x, s.touch.y)){
                s.setScreen(s.screenAbout);
            }
            if(btnExit.hit(s.touch.x, s.touch.y)){
                Gdx.app.exit();
            }
        }

        // события игры
        // ------------

        // отрисовка всего
        s.camera.update();
        s.batch.setProjectionMatrix(s.camera.combined);
        s.batch.begin();
        s.batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        btnGame.font.draw(s.batch, btnGame.text, btnGame.x, btnGame.y);
        btnSettings.font.draw(s.batch, btnSettings.text, btnSettings.x, btnSettings.y);
        btnAbout.font.draw(s.batch, btnAbout.text, btnAbout.x, btnAbout.y);
        btnExit.font.draw(s.batch, btnExit.text, btnExit.x, btnExit.y);
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
        imgBackGround.dispose();
    }

    void sleep(long time) {
        try{
            Thread.sleep(time);
        } catch (Exception ignored){
        }
    }
}
