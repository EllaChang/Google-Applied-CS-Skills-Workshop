/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordstack;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private StackedLayout stackedLayout;
    private String word1, word2;
    LinearLayout word1LinearLayout, word2LinearLayout;
    private Stack<LetterTile> placedTiles = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                words.add(word);
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        word1LinearLayout = findViewById(R.id.word1);
        // word1LinearLayout.setOnTouchListener(new TouchListener());
        word1LinearLayout.setOnDragListener(new DragListener());

        word2LinearLayout = findViewById(R.id.word2);
        // word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                    }
                    placedTiles.push(tile);
                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) {
        stackedLayout.clear();
        word1LinearLayout.removeAllViews();
        word2LinearLayout.removeAllViews();
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");
        Random r = new Random(System.currentTimeMillis());
        int i1 = r.nextInt(words.size()-1);
        int i2 = r.nextInt(words.size()-1);
        while (words.get(i1).length() > 8) i1 = r.nextInt(words.size()-1);
        while (i2 == i1 || words.get(i2).length() != words.get(i1).length()) i2 = r.nextInt(words.size()-1);
        word1 = words.get(i1);
        word2 = words.get(i2);
        String scrambled = scramble(word1, word2);
        for (int i = scrambled.length() - 1; i >= 0; i--) {
            LetterTile newTile = new LetterTile(this, scrambled.charAt(i));
            stackedLayout.push(newTile);
        }
        return true;
    }

    public boolean onUndo(View view) {
        if (!placedTiles.isEmpty()) {
            LetterTile tile = placedTiles.pop();
            tile.moveToViewGroup(stackedLayout);
        }
        return true;
    }

    public static String scramble(String word1, String word2) {
        String res = "";
        int size = word1.length();
        int i = 0, j = 0;
        Random r = new Random(System.currentTimeMillis());
        while (i < size || j < size) {
            int z = 1 + r.nextInt(10);
            if (z <= 5 && i < size)
                res += new Character(word1.charAt(i++)).toString();
            else if (z > 5 && j < size)
                res += new Character(word2.charAt(j++)).toString();
        }
        while (i < size)
            res += word1.substring(i);
        while (j < size)
            res += word2.substring(j);
        return res;
    }

    public boolean isValid(String s) {
        if (words.contains(s)) {
            return true;
        } else return false;
    }
}