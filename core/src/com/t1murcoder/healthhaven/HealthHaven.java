package com.t1murcoder.healthhaven;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;

public class HealthHaven extends Game {
	public static final float SCR_WIDTH = 1280, SCR_HEIGHT = 720;

	SpriteBatch batch; // ссылка на объект, отвечающий за вывод изображений
	OrthographicCamera camera; // пересчитывает все координаты под разные разрешения
	Vector3 touch; // хранит координаты касания экрана
	BitmapFont fontSmall, fontMedium, fontLarge; // шрифты

	ScreenIntro screenIntro;
	ScreenGame screenGame;
	ScreenSettings screenSettings;
	ScreenAbout screenAbout;

	// настройки игры
	boolean sound = true;
	boolean music = true;
	public static final int MODE_EASY = 0, MODE_NORMAL = 1, MODE_HARD = 2;
	int modeOfGame = MODE_EASY; // сложность игры
	String playerName = "Noname";

	@Override
	public void create () {
		batch = new SpriteBatch(); // создаём объект, отвечающий за вывод изображений
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touch = new Vector3();

		createFont();

		screenIntro = new ScreenIntro(this);
		screenGame = new ScreenGame(this);
		screenSettings = new ScreenSettings(this);
		screenAbout = new ScreenAbout(this);

		setScreen(screenIntro);
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	void createFont(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("spaceagecyrillic_regular.ttf"));
		//FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("comic.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
		parameter.size = 30;
		parameter.color = Color.valueOf("FFD700");
		parameter.borderWidth = 2;
		parameter.borderColor = Color.valueOf("A00000");
		fontSmall = generator.generateFont(parameter);
		parameter.size = 40;
		fontMedium = generator.generateFont(parameter);
		parameter.size = 50;
		fontLarge = generator.generateFont(parameter);

		generator.dispose();
	}
}
