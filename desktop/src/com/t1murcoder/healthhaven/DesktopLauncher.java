package com.t1murcoder.healthhaven;

import static com.t1murcoder.healthhaven.HealthHaven.SCR_HEIGHT;
import static com.t1murcoder.healthhaven.HealthHaven.SCR_WIDTH;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode((int)SCR_WIDTH, (int)SCR_HEIGHT);
		config.setForegroundFPS(60);
		config.setTitle("HealthHaven");
		new Lwjgl3Application(new HealthHaven(), config);
	}
}
