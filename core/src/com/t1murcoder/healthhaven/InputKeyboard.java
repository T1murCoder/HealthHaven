package com.t1murcoder.healthhaven;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class InputKeyboard {
    private final String fontName = "spaceagecyrillic_regular.ttf";
    private final String imageKeys = "keys.png";
    private final int fontSize = 20;

    private boolean endOfEdit;

    private final float x, y; // координаты
    private final float width, height; // ширина и высота всей клавиатуры
    private final float keyWidth, keyHeight; // ширина и высота каждой кнопки
    private final float padding; // расстояние между кнопками
    private final int textLength; // длина вводимого текста

    private BitmapFont font;

    private String text = ""; // вводимый текст
    private static final String LETTERS_EN_CAPS = "1234567890-~QWERTYUIOP+?^ASDFGHJKL;'`ZXCVBNM<> |";
    private static final String LETTERS_EN_LOW  = "!@#$%:&*()_~qwertyuiop[]^asdfghjkl:'`zxcvbnm,. |";
    private static final String LETTERS_RU_CAPS = "1234567890-~ЙЦУКЕНГШЩЗХЪ^ФЫВАПРОЛДЖЭ`ЯЧСМИТЬБЮЁ|";
    private static final String LETTERS_RU_LOW  = "!@#$%:&*()_~йцукенгшщзхъ^фывапролджэ`ячсмитьбюё|";
    private String letters = LETTERS_EN_CAPS;

    private final Texture imgAtlasKeys; // все изображения кнопок
    private final TextureRegion imgEditText; // поле ввода
    private final TextureRegion imgKeyUP, imgKeyDown; // кнопка выпуклая/вдавленная
    private final TextureRegion imgKeyBS, imgKeyEnter, imgKeyCL, imgKeySW; // картинки управляющих кноп

    private long timeStart, timeDuration = 150; // длительность надавливания кнопки
    private int keyPressed = -1; // код нажатой кнопки
    private final Array<Key> keys = new Array<>(); // список всех кноп

    public InputKeyboard(float scrWidth, float scrHeight, int textLength){
        generateFont();
        this.textLength = textLength; // количество вводимых символов

        imgAtlasKeys = new Texture(imageKeys);
        imgKeyUP = new TextureRegion(imgAtlasKeys, 0, 0, 256, 256);
        imgKeyDown = new TextureRegion(imgAtlasKeys, 256, 0, 256, 256);
        imgEditText = new TextureRegion(imgAtlasKeys, 256*2, 0, 256, 256);
        imgKeyBS = new TextureRegion(imgAtlasKeys, 256*3, 0, 256, 256);
        imgKeyEnter = new TextureRegion(imgAtlasKeys, 256*4, 0, 256, 256);
        imgKeyCL = new TextureRegion(imgAtlasKeys, 256*5, 0, 256, 256);
        imgKeySW = new TextureRegion(imgAtlasKeys, 256*6, 0, 256, 256);

        // задаём параметры клавиатуры
        width = scrWidth/21f*20; // ширина и высота клавиатуры
        height = scrHeight/5f*3;
        x = (scrWidth-width)/2; // координаты вывода клавиатуры
        y = height+scrHeight/30f;
        keyWidth = width/13; // ширина и высота каждой клавиши
        keyHeight = height/5;
        padding = 8; // отступы между кнопками
        createKBD();
    }

    // создание кнопок клавиатуры по рядам
    private void createKBD(){
        int j = 0;
        for (int i = 0; i < 12; i++, j++)
            keys.add(new Key(i*keyWidth+x+keyWidth/2, y-keyHeight*2, keyWidth-padding, keyHeight-padding, letters.charAt(j)));

        for (int i = 0; i < 13; i++, j++)
            keys.add(new Key(i*keyWidth+x, y-keyHeight*3, keyWidth-padding, keyHeight-padding, letters.charAt(j)));

        for (int i = 0; i < 12; i++, j++)
            keys.add(new Key(i*keyWidth+x+keyWidth/2, y-keyHeight*4, keyWidth-padding, keyHeight-padding, letters.charAt(j)));

        for (int i = 0; i < 11; i++, j++)
            keys.add(new Key(i*keyWidth+x+keyWidth, y-keyHeight*5, keyWidth-padding, keyHeight-padding, letters.charAt(j)));
    }

    // задаём/меняем раскладку символов на всех кнопках
    private void setCharsKBD() {
        int j = 0;
        for (int i = 0; i < 12; i++, j++)
            keys.get(j).letter = letters.charAt(j);

        for (int i = 0; i < 13; i++, j++)
            keys.get(j).letter = letters.charAt(j);

        for (int i = 0; i < 12; i++, j++)
            keys.get(j).letter = letters.charAt(j);

        for (int i = 0; i < 11; i++, j++)
            keys.get(j).letter = letters.charAt(j);
    }

    // рисуем клавиатуру и вводимый текст
    public void draw(SpriteBatch batch){
        // рисуем кнопки
        for (int i = 0; i < keys.size; i++) {
            drawImgKey(batch, i, keys.get(i).x, keys.get(i).y, keys.get(i).width, keys.get(i).height);
        }
        // рисуем вводимый текст
        batch.draw(imgEditText, 2*keyWidth+x+keyWidth/2, y-keyHeight, width-5*keyWidth-padding, keyHeight);
        font.draw(batch, text, 2*keyWidth+x+keyWidth/2, keys.get(0).letterY+keyHeight, width-5*keyWidth-padding, Align.center, false);
    }

    // рисуем каждую кнопку
    private void drawImgKey(SpriteBatch batch, int i, float x, float y, float width, float height){
        float dx, dy;
        if(keyPressed == i){ // если нажата, то рисуем нажатую кнопку
            batch.draw(imgKeyDown, x, y, width, height);
            dx = 2;
            dy = -2;
            if(TimeUtils.millis() - timeStart > timeDuration){
                keyPressed = -1;
            }
        } else { // рисуем отжатую кнопку
            dx = 0;
            dy = 0;
            batch.draw(imgKeyUP, x, y, width, height);
        }

        // выводим символы на кнопки
        switch (letters.charAt(i)) {
            case '~': batch.draw(imgKeyBS, x+dx, y+dy, width, height); break; // backspace
            case '^': batch.draw(imgKeyEnter, x+dx, y+dy, width, height); break; // enter
            case '`': batch.draw(imgKeyCL, x+dx, y+dy, width, height); break; // caps lock
            case '|': batch.draw(imgKeySW, x+dx, y+dy, width, height); break; // ru/en switcher
            default: // все прочие символы
                font.draw(batch, ""+keys.get(i).letter, keys.get(i).letterX+dx, keys.get(i).letterY+dy);
        }
    }

    // проверяем, куда нажали
    public boolean endOfEdit(float tx, float ty){
        for (int i = 0; i < keys.size; i++) {
            if(!keys.get(i).hit(tx, ty).equals("")){
                keyPressed = i;
                setText(i);
                timeStart = TimeUtils.millis();
            }
        }
        // окончание редактирования ввода (нажата кнопка enter)
        if(endOfEdit){
            endOfEdit = false;
            return true;
        }
        return false;
    }

    // обработка нажатия кнопок
    private void setText(int i){
        switch (letters.charAt(i)) {
            case '~': // backspace
                if(text.length()>0) text = text.substring(0, text.length() - 1);
                break;
            case '^': // enter
                if(text.length()==0) break;
                endOfEdit = true;
                break;
            case '`': // caps lock
                if(letters.charAt(12) == 'Q') letters = LETTERS_EN_LOW;
                else if(letters.charAt(12) == 'q') letters = LETTERS_EN_CAPS;
                else if(letters.charAt(12) == 'Й') letters = LETTERS_RU_LOW;
                else if(letters.charAt(12) == 'й') letters = LETTERS_RU_CAPS;
                setCharsKBD();
                break;
            case '|': // ru/en switcher
                if(letters.charAt(12) == 'й') letters = LETTERS_EN_LOW;
                else if(letters.charAt(12) == 'Й') letters = LETTERS_EN_CAPS;
                else if(letters.charAt(12) == 'q') letters = LETTERS_RU_LOW;
                else if(letters.charAt(12) == 'Q') letters = LETTERS_RU_CAPS;
                setCharsKBD();
                break;
            default: // ввод символов
                if(text.length()< textLength) text += letters.charAt(i);
                if(text.length() == 1 && letters == LETTERS_EN_CAPS) letters = LETTERS_EN_LOW;
                if(text.length() == 1 && letters == LETTERS_RU_CAPS) letters = LETTERS_RU_LOW;
                setCharsKBD();
        }
    }

    // выдача отредактированного текста
    public String getText() {
        return text;
    }

    // класс отдельной кнопки виртуальной клавиатуры
    private class Key {
        float x, y;
        float width, height;
        char letter; // символ на кнопке
        float letterX, letterY; // координаты вывода символа

        private Key (float x, float y, float width, float height, char letter) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.letter = letter;
            letterX = x + width/3;
            letterY = y + height - (height - font.getCapHeight())/2;
        }

        private String hit(float tx, float ty){
            if (x<tx && tx<x+width && y<ty && ty<y+height) {
                return "" + letter;
            }
            return "";
        }
    }

    private void generateFont(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontName));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = new Color(1, 1, 1, 1);
        parameter.size = fontSize;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 1;
        parameter.borderStraight = true;
        parameter.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
        System.out.println(parameter.characters);
        font = generator.generateFont(parameter);
        generator.dispose();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void dispose(){
        imgAtlasKeys.dispose();
        font.dispose();
    }
}
