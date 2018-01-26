package com.techacademy.takeda.ryoji.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

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

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }

    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                imageVIew.setImageURI(imageUri);
            } while (cursor.moveToNext());
        }
        cursor.close();

    }
}































