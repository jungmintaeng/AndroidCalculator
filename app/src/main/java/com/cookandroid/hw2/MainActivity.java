package com.cookandroid.hw2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import dalvik.bytecode.OpcodeInfo;

public class MainActivity extends AppCompatActivity {
    boolean hasResult = false;
    boolean nonNumberFlag;
    char prevInput = '0';
    Stack operator_Stack = new Stack();
    Stack number_Stack = new Stack();
    List<Object> operator_List = new ArrayList<Object>();
    List<Object> number_List = new ArrayList<Object>();
    Double result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup group = (RadioGroup)findViewById(R.id.radioG);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            TextView tv = (TextView) findViewById(R.id.result_view);
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                prevInput = '=';
                if(!hasResult)
                    return;
                RadioButton dec = (RadioButton)findViewById(R.id.Dec);

                if(dec.isChecked())
                {
                    Log.i("debug", result.toString() + " " + result.intValue());
                    if(result.intValue() == result)
                        //Log.i("debug", "DEC");
                        tv.setText(Integer.valueOf(result.intValue()).toString());
                    else
                        //Log.i("debug", "HEX");
                        tv.setText(result.toString());
                }
                else
                {
                    if(result.intValue() == result)
                        tv.setText(Integer.toHexString(result.intValue()).toString().toUpperCase());
                    else
                        tv.setText(Double.toHexString(result).toUpperCase().toString().toUpperCase());
                }
            }
        });
    }

    public void CompareOperator() {
        if (operator_List.size() == 0)
            return;

        char opInStack = (Character)operator_Stack.pop();
        char opCompare = (Character)operator_List.remove(0);

        /*
        1. 스택에 있는 연산자가 x나 / 일 경우
        2. 스택에 있는 연산자가 +나 -이고 새로운 연산자가 +나 -일 경우
        2. 스택에 있는 연산자가 +나 -고 새로운 연산자가 x나 /일 경우
         */

        Double numberInStack = (Double)number_Stack.pop();  //numberstack에 들어있는 숫자 하나를 pop 해옴
        Double numberInList = (Double)number_List.remove(0); //리스트의 맨 앞(들어온 순서)에 있는 숫자를 뺌

        //이 부분에서 number 스택의 상태는 숫자 하나, operator 스택의 상태는 아무것도 없는 상태이다.

        //1번의 경우
        if ((opInStack == '*') || (opInStack == '/')) {
            if (opInStack == '*') {
                number_Stack.push((Double)number_Stack.pop() * numberInStack);
                number_Stack.push(numberInList);
                operator_Stack.push(opCompare);
            } else if (opInStack == '/') {
                number_Stack.push((Double)number_Stack.pop() / numberInStack);
                number_Stack.push(numberInList);
                operator_Stack.push(opCompare);
            }
        }
        //2번의 경우와 3번의 경우를 한번에 계산
        else if ((opInStack == '+') || (opInStack == '-')) {
            if (opInStack == '+') {
                if ((opCompare == '+') || (opCompare == '-')) {
                    number_Stack.push((Double) number_Stack.pop() + numberInStack);
                    number_Stack.push(numberInList);
                    operator_Stack.push(opCompare);
                }
                else if (opCompare == '*') {
                    number_Stack.push(numberInStack * numberInList);
                    operator_Stack.push(opInStack);
                } else if (opCompare == '/') {
                    number_Stack.push(numberInStack / numberInList);
                    operator_Stack.push(opInStack);
                }

            }
            else if (opInStack == '-') {
                if ((opCompare == '+') || (opCompare == '-')) {
                    number_Stack.push((Double)number_Stack.pop() - numberInStack);
                    number_Stack.push(numberInList);
                    operator_Stack.push(opCompare);
                } else if (opCompare == '*') {
                    number_Stack.push(numberInStack * numberInList);
                    operator_Stack.push(opInStack);
                } else if (opCompare == '/') {
                    number_Stack.push(numberInStack / numberInList);
                    operator_Stack.push(opInStack);
                }
            }
        }
    }

    public void Onclick(View v) {
        TextView tv = (TextView) findViewById(R.id.result_view);
        if (prevInput == '=')
            tv.setText("");

        nonNumberFlag = (prevInput == '.') | (prevInput == '+') | (prevInput == '-') | (prevInput == 'x') | (prevInput == '/');

        switch (v.getId()) {
            case R.id.Equals: {
                if(tv.getText() == "")
                    return;
                int i;

                if (nonNumberFlag)
                    tv.setText(tv.getText().subSequence(0, tv.getText().length() - 1));

                String string_TextBox = tv.getText().toString();

                for (i = 0; i < string_TextBox.length(); i++) {
                    if (string_TextBox.charAt(i) == '＋')
                        operator_List.add('+');
                    else if (string_TextBox.charAt(i) == '－')
                        operator_List.add('-');
                    else if (string_TextBox.charAt(i) == '×')
                        operator_List.add('*');
                    else if (string_TextBox.charAt(i) == '÷')
                        operator_List.add('/');
                }

                String[] numbers = string_TextBox.split("[＋－×÷]");

                for (i = 0; i < numbers.length; i++) {
                    number_List.add(Double.parseDouble(numbers[i]));
                }

                if (number_List.size() < 2)
                {
                    Clear();
                    prevInput = '=';
                    return;
                }

                //숫자가 두 개 이상 들어오지 않았으면 취할 행동이 없으므로 return

                //숫자와 연산자를 나누어서 각각의 리스트에 저장-----------


                //--이 아래부터 연산하는 동작
                /*
                1. 리스트에서 숫자를 하나 뽑아와서 push
                2. 숫자 다음의 연산자 하나를 뽑아와서 push
                3. 피연산 숫자 push
                 */

                int indexOfNumbers = 0; //스트링 배열에서 빼올 때, 이미 빠진 숫자의 인덱스를 계산한다.

                number_Stack.push(number_List.remove(0));
                number_Stack.push(number_List.remove(0));

                operator_Stack.push(operator_List.remove(0));   //현재 스택에 두 개의 숫자와 한 개의 연산자가 들어가 있음


                while (operator_List.size() != 0) {
                    CompareOperator();
                }

                /*
                while문 탈출 --> 스택에 숫자 두개,, 연산자 하나만 남은 상태
                이후에 연산자만 구분해서 연산만 해주면 결과가 나옴
                 */

                double next_Number = (Double)number_Stack.pop();
                double prev_Number = (Double)number_Stack.pop();

                char last_Operator = (Character) operator_Stack.pop();

                switch (last_Operator)
                {
                    case '+':
                    {
                        result = prev_Number + next_Number;
                        break;
                    }
                    case '-':
                    {
                        result = prev_Number - next_Number;
                        break;
                    }
                    case '*':
                    {
                        result = prev_Number * next_Number;
                        break;
                    }
                    case '/':
                    {
                        result = prev_Number / next_Number;
                        break;
                    }
                }

                RadioButton dec = (RadioButton)findViewById(R.id.Dec);

                if(dec.isChecked())
                {
                    if(result.intValue() == result)
                        tv.setText(string_TextBox + "= " + result.intValue());
                    else
                        tv.setText(string_TextBox + "= " + result.toString());
                }
                else
                {
                    if(result.intValue() == result)
                        tv.setText(Integer.toHexString(result.intValue()).toString().toUpperCase());
                    else
                        tv.setText(Double.toHexString(result).toString().toUpperCase());
                }

                Clear();
                prevInput = '=';
                hasResult = true;
                break;
            }
            case R.id.One:
                tv.setText(tv.getText() + "1");
                prevInput = '1';
                break;
            case R.id.Two: {
                tv.setText(tv.getText() + "2");
                prevInput = '2';
                break;
            }
            case R.id.Three: {
                tv.setText(tv.getText() + "3");
                prevInput = '3';
                break;
            }
            case R.id.Four: {
                tv.setText(tv.getText() + "4");
                prevInput = '4';
                break;
            }
            case R.id.Five: {
                tv.setText(tv.getText() + "5");
                prevInput = '5';
                break;
            }
            case R.id.Six: {
                tv.setText(tv.getText() + "6");
                prevInput = '6';
                break;
            }
            case R.id.Seven: {
                tv.setText(tv.getText() + "7");
                prevInput = '7';
                break;
            }
            case R.id.Eight: {
                tv.setText(tv.getText() + "8");
                prevInput = '8';
                break;
            }
            case R.id.Nine: {
                tv.setText(tv.getText() + "9");
                prevInput = '9';
                break;
            }
            case R.id.Zero: {
                tv.setText(tv.getText() + "0");
                prevInput = '0';
                break;
            }
            case R.id.Plus: {
                if (tv.getText() != "") {
                    if (nonNumberFlag)
                        tv.setText(tv.getText().subSequence(0, tv.getText().length() - 1));
                    tv.setText(tv.getText() + "＋");
                    prevInput = '+';
                }

                break;
            }
            case R.id.Minus: {
                if (tv.getText() != "") {
                    if (nonNumberFlag)
                        tv.setText(tv.getText().subSequence(0, tv.getText().length() - 1));
                    tv.setText(tv.getText() + "－");
                    prevInput = '-';
                }
                break;
            }
            case R.id.Divide: {
                if (tv.getText() != "") {
                    if (nonNumberFlag)
                        tv.setText(tv.getText().subSequence(0, tv.getText().length() - 1));
                    tv.setText(tv.getText() + "÷");
                    prevInput = '/';
                }

                break;
            }
            case R.id.Times: {
                if (tv.getText() != "") {
                    if (nonNumberFlag)
                        tv.setText(tv.getText().subSequence(0, tv.getText().length() - 1));
                    tv.setText(tv.getText() + "×");
                    prevInput = 'x';
                }

                break;
            }
            case R.id.Point: {
                if (tv.getText() != "") {
                    if (nonNumberFlag)
                        tv.setText(tv.getText().subSequence(0, tv.getText().length() - 1));
                    if (tv.getText() != "")
                        tv.setText(tv.getText() + ".");
                    prevInput = '.';
                }
                break;
            }

        }

    }

    public void Clear()
    {
        operator_Stack.clear();
        number_Stack.clear();
        number_List.clear();
        operator_List.clear();
    }
}
