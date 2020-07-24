package com.example.scarnesdice;

import androidx.appcompat.app.AppCompatActivity;
import java.util.*;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.OnClickAction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    ImageView die;
    Button roll, hold, reset;
    TextView userTurn, userTotal, compTurn, compTotal;

    int userTurnScore = 0, userTotalScore = 0;
    int compTurnScore = 0, compTotalScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        die = (ImageView) findViewById(R.id.die);
        roll = (Button) findViewById(R.id.roll);
        hold = (Button) findViewById(R.id.hold);
        reset = (Button) findViewById(R.id.reset);
        userTurn = (TextView) findViewById(R.id.userTurn);
        userTotal = (TextView) findViewById(R.id.userTotal);
        compTurn = (TextView) findViewById(R.id.compTurn);
        compTotal = (TextView) findViewById(R.id.compTotal);

        roll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (rollDice(1) == 1) {
                    userTurn.setText("Your turn score: " + userTurnScore);
                    userTotal.setText("Your total score: " + userTotalScore);
                    computerTurn();
                }
                userTurn.setText("Your turn score: " + userTurnScore);
                userTotal.setText("Your total score: " + userTotalScore);
            }
        });

        hold.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userTotalScore += userTurnScore;
                userTurnScore = 0;
                userTurn.setText("Your turn score: " + userTurnScore);
                userTotal.setText("Your total score: " + userTotalScore);
                computerTurn();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userTurnScore = 0;
                userTotalScore = 0;
                compTurnScore = 0;
                compTotalScore = 0;
                userTurn.setText("Your turn score: " + userTurnScore);
                userTotal.setText("Your total score: " + userTotalScore);
                compTurn.setText("Computer turn score: " + compTurnScore);
                compTotal.setText("Computer total score: " + compTotalScore);
                roll.setEnabled(true);
                hold.setEnabled(true);
            }
        });

    }

    public int rollDice(int player) {
        int dice = 0;
        Random r = new Random(System.currentTimeMillis());
        int index = 1 + r.nextInt(6);
        switch (index) {
            case 1:
                die.setImageResource(R.drawable.dice1);
                if (player == 1) userTurnScore = 0;
                else compTurnScore = 0;
                dice = 1;
                break;
            case 2:
                die.setImageResource(R.drawable.dice2);
                if (player == 1) userTurnScore += 2;
                else compTurnScore += 2;
                dice = 2;
                break;
            case 3:
                die.setImageResource(R.drawable.dice3);
                if (player == 1) userTurnScore += 3;
                else compTurnScore += 3;
                dice = 3;
                break;
            case 4:
                die.setImageResource(R.drawable.dice4);
                if (player == 1) userTurnScore += 4;
                else compTurnScore += 4;
                dice = 4;
                break;
            case 5:
                die.setImageResource(R.drawable.dice5);
                if (player == 1) userTurnScore += 5;
                else compTurnScore += 5;
                dice = 5;
                break;
            case 6:
                die.setImageResource(R.drawable.dice6);
                if (player == 1) userTurnScore += 6;
                else compTurnScore += 6;
                dice = 6;
                break;
        }
        return dice;
    }

    Handler handler = new Handler();
    Runnable run = new Runnable() {
        @Override
        public void run() {
            computerTurn();
        }
    };

    public void computerTurn() {
        if (compRoll()) handler.postDelayed(run, 1000);
        else {
            roll.setEnabled(true);
            hold.setEnabled(true);
        }
    }

    public boolean compRoll() {
        roll.setEnabled(false);
        hold.setEnabled(false);
        if (rollDice(2) == 1 || compTurnScore > 10) {
            compTotalScore += compTurnScore;
            compTurnScore = 0;
            compTurn.setText("Computer turn score: " + compTurnScore);
            compTotal.setText("Computer total score: " + compTotalScore);
            return false;
        }
        compTurn.setText("Computer turn score: " + compTurnScore);
        compTotal.setText("Computer total score: " + compTotalScore);
        return true;
    }

}