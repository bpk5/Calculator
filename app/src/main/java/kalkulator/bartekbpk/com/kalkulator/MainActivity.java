package kalkulator.bartekbpk.com.kalkulator;

import java.math.BigDecimal;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String valueString; // Wartość do wyświetlenia jako string.
    String valueAllString; // Wynik ale jako string.
    BigDecimal valueAll; // Wynik - "float".
    BigDecimal valueLeft; // Wartość do obliczeń, lewa część działania.
    BigDecimal valueRight; // Wartość do obliczeń, prawa część działania.
    Boolean flagOperation;
    Boolean flagOperationDouble; // Zapobieganie kilkukrotnemu kliknięciu
    Boolean flagValueLeft; // Flaga mówiąca czy wstawiono wartość do valueLeft.
    Boolean error; // Flaga błędu.
    Sign signFirst; // Pierwsza operacja.

    BigDecimal zero; // BigDecimal z wartością zero.
    BigDecimal zeroZero; // BigDeciaml z wartością 0.0
    BigDecimal oneHundred; // 100
    BigDecimal one; // Wartość jeden

    TextView textView;
    TextView textViewLeft;
    TextView textViewSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zero = new BigDecimal("0");
        zeroZero = new BigDecimal("0.0");
        oneHundred = new BigDecimal("100");
        one = new BigDecimal("1");

        textView = (TextView) findViewById(R.id.textView);
        textViewLeft = (TextView) findViewById(R.id.textViewLeft);
        textViewSign = (TextView) findViewById(R.id.textViewSign);
        setDefaultValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void menuAboutClick(MenuItem item) {
        Context context = getApplicationContext();
        CharSequence text = "Kalkulator \n\n version: 1.1 \n author: Bartłomiej Kulesa \n\n 2017";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, -200);
        toast.show();
    }

    public enum Sign {
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        SCORE,
        EMPTY;
    }
    public void button0Click(View view) {
        if (!error) {
            if ((valueString != "0") && (flagOperation == false)) {
                valueString = valueString + "0";
            }
            else if (flagOperation == true) {
                valueString = "0";
            }

            flagOperation = false;
            flagOperationDouble = false;    // Odwołanie flagi, działanie może się wykonać.
            refreshValue();                 // Odświeżanie wartości w textView.
        }
    }

    public void button1Click(View view) {
        setNumber("1");
    }

    public void button2Click(View view) {
        setNumber("2");
    }

    public void button3Click(View view) {
        setNumber("3");
    }

    public void button4Click(View view) {
        setNumber("4");
    }

    public void button5Click(View view) {
        setNumber("5");
    }

    public void button6Click(View view) {
        setNumber("6");
    }

    public void button7Click(View view) {
        setNumber("7");
    }

    public void button8Click(View view) {
        setNumber("8");
    }

    public void button9Click(View view) {
        setNumber("9");
    }

    public void buttonCeClick(View view) {
        if (!error) {
            if (!valueString.isEmpty()) {
                valueString = valueString.substring(0, valueString.length() - 1);
            }
        }
        else {
            clearError();
            setDefaultValue(); // Ustawienia początkowe.
        }
        refreshValue();
    }

    public void buttonPointClick(View view) {
        if (!error) {
            if ((valueString.indexOf('.') < 0) && (flagOperation == false)) {
                valueString = valueString + ".";
            }
            else if (flagOperation == true) {
                valueString = "0.";
            }

            flagOperation = false;
            flagOperationDouble = false;
            refreshValue();
        }
    }

    public void buttonProcentClick(View view) {
        valueRight = new BigDecimal(textView.getText().toString());

        // Dodaj procent do liczby lub odejmij.
        if ((signFirst == Sign.PLUS) || (signFirst == Sign.MINUS)) {
            valueRight = valueRight.divide(oneHundred, 6, BigDecimal.ROUND_FLOOR);
            valueRight = valueLeft.multiply(valueRight);
        }
        // Obliczanie procentu danej liczby.
        else if (signFirst == Sign.MULTIPLY) {
            valueRight = valueRight.divide(oneHundred, 6, BigDecimal.ROUND_FLOOR);
            valueRight = valueLeft.multiply(valueRight); // Wyliczenie prawej strony.
            valueLeft = valueRight; // Przypisanie prawej wartości do lewej
            // Mnożenie wykona się ale prawa strona będzie równa 1. Wynik się nie zmieni.
            valueRight = one; // Prawa na 1.
        }
        // Obliczanie liczby gdy dany jest jej procent.
        else if (signFirst == Sign.DIVIDE) {
            valueLeft = valueLeft.multiply(oneHundred); // Wyliczenie prawej strony.
            valueLeft = valueLeft.divide(valueRight, 6, BigDecimal.ROUND_FLOOR);
            valueRight = one;
        }

        setOperation(Sign.SCORE);
    }

    public void buttonEqualClick(View view) {
        valueRight = new BigDecimal(textView.getText().toString());
        setOperation(Sign.SCORE);
    }

    public void buttonPlusClick(View view) {
        valueRight = new BigDecimal(textView.getText().toString());
        setOperation(Sign.PLUS);
    }

    public void buttonMinusClick(View view) {
        valueRight = new BigDecimal(textView.getText().toString());
        setOperation(Sign.MINUS);
    }

    public void buttonMultiplyClick(View view) {
        valueRight = new BigDecimal(textView.getText().toString());
        setOperation(Sign.MULTIPLY);
    }

    public void buttonDivideClick(View view) {
        valueRight = new BigDecimal(textView.getText().toString());
        setOperation(Sign.DIVIDE);
    }

    public void buttonCleanClick(View view) {
        clearError(); // Kasowanie błędu.
        setDefaultValue(); // Ustawienia początkowe.
        refreshValue(); // Odświeżanie wartości w textView.
    }

    /**
     * Odświeżanie wartości w textView.
     */
    private void refreshValue() {
        textView.setText(valueString);
        textViewLeft.setText(valueAllString);
    }

    /**
     * Kasowanie wszystkiego na wyświetlaczu (textView).
     */
    private void checkValue() {
        if ((valueString == "0") || (flagOperation == true)) {
            valueString = "";
            flagOperation = false;
        }
    }

    /**
     * Wykonanie operacji matematycznych na wprowadzonych danych.
     * @param signSecond znak, działanie matematyczne
     */
    private void doOperation(Sign signSecond) {
        flagOperation = true; // Będzie wykonywana jakaś operacja. Potrzebne do czyszczenia.

        if (flagValueLeft == false) {
            valueLeft = new BigDecimal(textView.getText().toString());
            flagValueLeft = true;
            valueAll = valueLeft;
            valueAllString = valueLeft.toString();
        } else if (flagOperationDouble == false) {

            switch (signFirst) {
                case PLUS:
                    valueAll = valueLeft.add(valueRight);
                    break;
                case MINUS:
                    valueAll = valueLeft.subtract(valueRight);
                    break;
                case MULTIPLY:
                    valueAll = valueLeft.multiply(valueRight);
                    break;
                case DIVIDE:
                    if ((!valueRight.equals(zero)) && (!valueRight.equals(zeroZero))) {
                        // Zaokrąglenie do 6 znaków po przecinku.
                        valueAll = valueLeft.divide(valueRight, 6, BigDecimal.ROUND_FLOOR);
                    }
                    else {
                        error = true; // Wykryto dzielenie przez zero.
                    }
                    break;
                default:
            }
            // Sprawdzenie czy wystąpił błąd.
            if (error == true) {
                valueString = "ERROR";
            }
            else {
                valueLeft = valueAll; // Przypisanie wyniku do lewej strony.
                valueString = valueAll.toString();
                valueAllString = valueLeft.toString();
            }
        }

        deleteLastZero();

        signFirst = signSecond;
        writeSign(signFirst); // Wywołanie metody wypisującej znak.
        flagOperationDouble = true; // Zabezpieczenie przed ponownym wykonaniem działania.
        refreshValue();
    }

    /**
     * Kasuje niepotrzebne zera na końcu liczby (te po przecinku).
     */
    private void deleteLastZero() {
        boolean isZero = true;
        // Kasowanie niepotrzebnych zer na końcu po przecinku.
        while (isZero) {
            char sig = valueString.charAt(valueString.length() - 1);
            if (((sig == '0') || (sig == '.')) && (valueString.indexOf('.') >= 0)) {
                valueString = valueString.substring(0, valueString.length() - 1);
            }
            else {
                isZero = false;
            }
        }

        isZero = true;
        // Kasowanie niepotrzebnych zer na końcu po przecinku dla textViewLeft
        while (isZero) {
            char sig = valueAllString.charAt(valueAllString.length() - 1);
            if (((sig == '0') || (sig == '.')) && (valueAllString.indexOf('.') >= 0)) {
                valueAllString = valueAllString.substring(0, valueAllString.length() - 1);
            }
            else {
                isZero = false;
            }
        }
    }

    /**
     * Wstawia odpowiedni znak do textViewSign.
     * @param sign działanie matematyczne (enum)
     */
    private void writeSign(Sign sign) {
        switch (sign) {
            case PLUS:
                textViewSign.setText("+");
                break;
            case MINUS:
                textViewSign.setText("-");
                break;
            case MULTIPLY:
                textViewSign.setText("x");
                break;
            case DIVIDE:
                textViewSign.setText("/");
                break;
            case SCORE:
                textViewSign.setText("=");
                break;
            default:
        }
    }

    /**
     * Kasowanie flagi błędu.
     * Wywołanie metody ustawiającej wartości domyślne.
     */
    private void clearError() {
        error = false;
        setDefaultValue();
    }

    /**
     * Ustawienia domyślne, resetowanie wartości.
     */
    private void setDefaultValue() {
        valueString = "0";
        valueAllString = "0";
        valueAll = new BigDecimal("0");
        valueLeft = new BigDecimal("0");
        valueRight = new BigDecimal("0");
        flagOperation = false;
        flagOperationDouble = false;
        flagValueLeft = false;
        error = false;
        signFirst = Sign.EMPTY;
        textViewSign.setText("");
    }

    /**
     * Uniwersalna metoda do wprowadzania znaku z klawiatury na ekran (textViem).
     * Wywoływana przez onClick przycisków 1 - 9
     * @param number liczba (String) wprowadzona z klawiatury,
     */
    private void setNumber(String number) {
        if (!error) {
            checkValue();
            valueString = valueString + number;
            flagOperationDouble = false;
            refreshValue();
        }
    }

    /**
     * Wywołuje metodę wykonującą działanie matematyczne.
     * @param sign znak działania matematycznego
     */
    private void setOperation(Sign sign) {
        // Jeżeli nie ma błędu to wywołąj metodę wykonującą działąnie matematyczne.
        if (!error) {
            doOperation(sign);
        }
    }

}