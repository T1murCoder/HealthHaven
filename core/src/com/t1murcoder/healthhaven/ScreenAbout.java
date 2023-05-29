package com.t1murcoder.healthhaven;


import static com.t1murcoder.healthhaven.HealthHaven.SCR_HEIGHT;
import static com.t1murcoder.healthhaven.HealthHaven.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class ScreenAbout implements Screen {
    HealthHaven s;
    Texture imgBackGround; // фон
    HavenButton btnBack;

    String textAbout =
            "Игра Health Haven\n" +
            "создана в рамках\n" +
            "проекта Mobile Game\n" +
            "Development на Java\n" +
            "с использованием\n" +
            "фреймворка LibGDX.\n\n" +
            "Цель игры: продержаться\n" +
            "как можно дольше,\n" +
            "собирая хорошие предметы\n" +
            "и уклоняясь от плохих.";

    public ScreenAbout(HealthHaven healthHaven) {
        s = healthHaven;
        imgBackGround = new Texture("bg_about.png");
        // создаём кнопки
        btnBack = new HavenButton(s.fontLarge, "BACK", 200, 150);
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

            if(btnBack.hit(s.touch.x, s.touch.y)){
                s.setScreen(s.screenIntro);
            }
        }

        // события игры
        // ------------

        // отрисовка всего
        s.camera.update();
        s.batch.setProjectionMatrix(s.camera.combined);
        s.batch.begin();
        s.batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        s.fontSmall.draw(s.batch, textAbout, 50, 650);
        btnBack.font.draw(s.batch, btnBack.text, btnBack.x, btnBack.y);
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
}
