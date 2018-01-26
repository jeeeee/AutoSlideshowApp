package com.techacademy.takeda.ryoji.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/*
スライドショーアプリ
3つのボタン（進む、戻る、再生/停止）を使って、3つ以上の画像を順に自動送りで表示するスライドショーアプリを作成してください。

下記の要件を満たしてください。

プロジェクトを新規作成し、 AutoSlideshowApp というプロジェクト名をつけてください
スライドさせる画像は、Android端末に保存されているGallery画像を表示させてください（つまり、ContentProviderの利用）
画面には画像と3つのボタン（進む、戻る、再生/停止）を配置してください
進むボタンで1つ先の画像を表示し、戻るボタンで1つ前の画像を表示します
最後の画像の表示時に、進むボタンをタップすると、最初の画像が表示されるようにしてください
最初の画像の表示時に、戻るボタンをタップすると、最後の画像が表示されるようにしてください
再生ボタンをタップすると自動送りが始まり、2秒毎にスライドさせてください
自動送りの間は、進むボタンと戻るボタンはタップ不可にしてください
再生ボタンをタップすると停止ボタンになり、停止ボタンをタップすると再生ボタンにしてください
停止ボタンをタップすると自動送りが止まり、進むボタンと戻るボタンをタップ可能にしてください
ユーザがパーミッションの利用を「拒否」した場合にも、アプリの強制終了やエラーが発生しない
要件を満たすものであれば、どのようなものでも構いません。
見栄え良く、楽しめるスライドショーアプリを目指しましょう！

 */

public class MainActivity extends AppCompatActivity {

    Timer mTimer;
    Handler mHandler = new Handler();
    Cursor cursor;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    ImageView imageView;
    ImageButton nextButton;
    ImageButton previousButton;
    ImageButton playButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        playButton = findViewById(R.id.playButton);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!(mTimer == null)) {
            mTimer.cancel();
            mTimer = null;
        }
        if (!(cursor == null)) {
            cursor.close();
        }
    }

    private void getContentsInfo() {
        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (!cursor.moveToFirst()) {
            Toast.makeText(this, "画像がありません。", Toast.LENGTH_LONG).show();
            nextButton.setEnabled(false);
            previousButton.setEnabled(false);
            playButton.setEnabled(false);
        }
    }

    private void getContents() {
        // indexからIDを取得し、そのIDから画像のURIを取得する
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        imageView.setImageURI(imageUri);
    }

    private void moveToNext() {
        if (!cursor.moveToNext()) {
            cursor.moveToFirst();
        }
    }

    private void moveToPrevious() {
        if (!cursor.moveToPrevious()) {
            cursor.moveToLast();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextButton:
                getContents();
                moveToNext();
                break;
            case R.id.previousButton:
                getContents();
                moveToPrevious();
                break;
            case R.id.playButton:
                if (mTimer == null) {
                    nextButton.setEnabled(false);
                    previousButton.setEnabled(false);
                    playButton.setImageResource(android.R.drawable.ic_media_pause);
                    playTask();
                } else {
                    nextButton.setEnabled(true);
                    previousButton.setEnabled(true);
                    playButton.setImageResource(android.R.drawable.ic_media_play);
                    mTimer.cancel();
                    mTimer = null;
                }
                break;
            default:
                break;
        }
    }

    public void playTask() {
        // タイマーの始動
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getContents();
                        moveToNext();
                    }
                });
            }
        }, 100, 2000);    // 最初に始動させるまで 100ミリ秒、ループの間隔を 2000ミリ秒 に設定
    }
}































