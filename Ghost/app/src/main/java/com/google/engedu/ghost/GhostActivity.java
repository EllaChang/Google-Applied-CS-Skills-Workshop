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

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.*;

import java.io.*;
import java.util.*;

public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private String frag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            System.out.println("error loading dictionary");
        }

        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        frag = "";
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    public boolean challenge(View view) {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (frag.length() >= 4 && dictionary.isWord(frag)) {
            label.setText("You win!");
        } else {
            String word = dictionary.getAnyWordStartingWith(frag);
            if (word != null) {
                label.setText("Computer wins! A possible word is: " + word);
            } else label.setText("You win!");
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView text = (TextView) findViewById(R.id.ghostText);

        // the computer wins if the user has just formed a word
        if (frag.length() >= 4 && dictionary.isWord(frag)) {
            label.setText("Computer wins! You just formed a word.");
            return;
        }

        // otherwise write the next character of a possible word
        else {
            String word = dictionary.getAnyWordStartingWith(frag);
            // if no words can be formed, computer wins
            if (word == null || word.equals(frag)) {
                // computer challenges the player
                label.setText("Computer wins! No words can be formed.");
                return;
            } else {
                // add the next letter of it to frag
                if (word.length() > frag.length()) {
                    frag += word.charAt(frag.length());
                }
                text.setText(frag);
            }
        }

        // now it's the user's turn
        label.setText(USER_TURN);
        userTurn = true;
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char c = (char) event.getUnicodeChar();
        if (Character.isLetter(c)) {
            frag += c;
            TextView text = (TextView) findViewById(R.id.ghostText);
            text.setText(frag);
            userTurn = false;
            computerTurn();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}