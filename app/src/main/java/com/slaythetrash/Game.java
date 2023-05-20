package com.slaythetrash;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Random;

public class Game extends AppCompatActivity {
    private ImageView trashImageView;
    private ImageView bin1ImageView;
    private ImageView bin2ImageView;
    private ImageView bin3ImageView;
    private TextView scoreTextView;
    private ViewGroup container;
    private Activity activity;

    private int score = 0;
    private int highScore = 0;
    private boolean isGameOver = false;
    private int[] plasticTrashImages = {
            R.drawable.plastic_cup,
            R.drawable.plastic_bag1,
            R.drawable.plastic_bag2
    };

    private int[] glassTrashImages = {
            R.drawable.glass_bottle1,
            R.drawable.glass_bottle2,
    };

    private int[] paperTrashImages = {
            R.drawable.papers,
            R.drawable.cardboard
    };

    private int[] binImages = {
            R.drawable.plastic,
            R.drawable.paper,
            R.drawable.glass
    };

    private Random random = new Random();

    private void generateTrash() {
            int containerWidth = container.getWidth();
            int containerHeight = container.getHeight();

            trashImageView.setX(containerWidth / 2f - trashImageView.getWidth() / 2f);
            trashImageView.setY(containerHeight / 1.5f - trashImageView.getHeight() / 2f);
        int trashType = new Random().nextInt(3);

        // Получение массива изображений мусора в зависимости от типа
        int[] trashImages;
        if (trashType == 0) {
            trashImages = plasticTrashImages;
        } else if (trashType == 1) {
            trashImages = paperTrashImages;
        } else {
            trashImages = glassTrashImages;
        }
        int randomIndex = new Random().nextInt(trashImages.length);

        trashImageView.setImageResource(trashImages[randomIndex]);
        trashImageView.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY;
            private float offsetX, offsetY;
            private float originalX, originalY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startX = event.getRawX();
                    startY = event.getRawY();
                    offsetX = v.getX() - startX;
                    offsetY = v.getY() - startY;
                    originalX = v.getX();
                    originalY = v.getY();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float newX = event.getRawX() + offsetX;
                    float newY = event.getRawY() + offsetY;
                    v.setX(newX);
                    v.setY(newY);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((isInBin(trashImageView, bin1ImageView) && checkTrashType(trashType, 0))) {
                        increaseScore();
                    } else if ((isInBin(trashImageView, bin2ImageView) && checkTrashType( trashType, 1))) {
                        increaseScore();
                    } else if ((isInBin(trashImageView, bin3ImageView) && checkTrashType(trashType, 2))) {
                        increaseScore();
                    } else if((isInBin(trashImageView, bin1ImageView)) || (isInBin(trashImageView, bin2ImageView))|| (isInBin(trashImageView, bin3ImageView))){
                        gameOver();
                    }
                    else {
                        v.setX(originalX);
                        v.setY(originalY);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        int trashType = 0;
        highScore = loadHighScore();
        TextView recordTextView = findViewById(R.id.recordTextView);
        recordTextView.setText("Рекорд: " + highScore);
        container = findViewById(R.id.layer);
        scoreTextView =  findViewById(R.id.scoreTextView);
        trashImageView = findViewById(R.id.trashImageView);
        bin1ImageView = findViewById(R.id.bin1ImageView);
        bin2ImageView = findViewById(R.id.bin2ImageView);
        bin3ImageView = findViewById(R.id.bin3ImageView);
        // Установка случайного изображения мусора на ImageView
        generateTrash();
    }
    private boolean isInBin(ImageView trashImageView, ImageView binImageView) {
        Rect trashRect = new Rect();
        trashImageView.getHitRect(trashRect);

        Rect binRect = new Rect();
        binImageView.getHitRect(binRect);

        return Rect.intersects(trashRect, binRect);
    }
    private boolean checkTrashType(int trashType, int binType) {
        return trashType == binType;
    }

    private void gameOver() {
        isGameOver = true;

        // Отображение окна поражения
        Dialog gameOverDialog = new Dialog(Game.this);
        gameOverDialog.setContentView(R.layout.dialog_game_over);
        if (score >= highScore) {
            TextView resultTextView = gameOverDialog.findViewById(R.id.resultTextView);
            resultTextView.setText("Вы побили свой рекорд!" +"\nСчёт: " + score + "\nПрошлый рекорд: " + highScore);
            highScore = score;
            saveHighScore(highScore);
        }
        else{
            TextView resultTextView = gameOverDialog.findViewById(R.id.resultTextView);
            resultTextView.setText("Вы проиграли!" +"\nСчёт: " + score + "\nРекорд: " + highScore);
        }
        TextView menuButton = gameOverDialog.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Возврат в меню
                Intent intent = new Intent(Game.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button startAgainButton = gameOverDialog.findViewById(R.id.playAgain);
        startAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Начать заново
                Intent intent = new Intent(Game.this, Game.class);
                startActivity(intent);
                finish();
            }
        });

        gameOverDialog.show();
    }
    private void updateScore() {
        scoreTextView.setText("Счёт: " + score);
        if (score > highScore) {
            TextView recordTextView = findViewById(R.id.recordTextView);
            recordTextView.setText("Рекорд: " + score);
        }
        generateTrash();
    }

    // Метод вызывается, когда игрок правильно сортирует мусор
    private void increaseScore() {
        score++;
        updateScore();
    }
    private void saveHighScore(int score) {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("HighScore", score);
        editor.apply();
    }

    // Метод для загрузки рекорда
    private int loadHighScore() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("HighScore", 0);
    }

}