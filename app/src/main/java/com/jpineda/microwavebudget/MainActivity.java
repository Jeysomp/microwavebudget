package com.jpineda.microwavebudget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

import de.nitri.gauge.Gauge;

public class MainActivity extends AppCompatActivity {
    int transmitterPo = 0;
    int transmitterGain = 0;
    int transmitterLoss = 0;

    double propagationD = 0;
    double propagationF = 0;

    TextView txtPropagationFSL;
    double FSPL = 0;

    int receptorGain = 0;
    int receptorLoss = 0;
    int receptorLm = 0;

    double lBudget = 0;

    Gauge gauge;
    int currentValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gauge = findViewById(R.id.gauge);
        currentValue = -135;
        gauge.setValue(currentValue);

        FSPL = 0;
        txtPropagationFSL = findViewById(R.id.txt_propagation_FSL);

        EditText edtPo = findViewById(R.id.edt_Po);
        EditTextChangeListener edtPo_Listener = new EditTextChangeListener(edtPo, EditTextChangeListener.EDT_TRANSMITTER_P0);

        EditText edtTransmitterGain = findViewById(R.id.edt_transmitter_gain);
        EditTextChangeListener edtTGain_Listener = new EditTextChangeListener(edtTransmitterGain, EditTextChangeListener.EDT_TRANSMITTER_GAIN);

        EditText edtTransmitterLoss = findViewById(R.id.edt_transmitter_loss);
        EditTextChangeListener edtTLoss_Listener = new EditTextChangeListener(edtTransmitterLoss, EditTextChangeListener.EDT_TRANSMITTER_LOSS);


        EditText edtPropagationD = findViewById(R.id.edt_propagation_d);
        EditTextChangeListener edtPropagationD_Listener = new EditTextChangeListener(edtPropagationD, EditTextChangeListener.EDT_PROPAGATION_D);

        EditText edtPropagationF = findViewById(R.id.edt_propagation_f);
        EditTextChangeListener edtPropagationF_Listener = new EditTextChangeListener(edtPropagationF, EditTextChangeListener.EDT_PROPAGATION_F);


        EditText edtReceptorGain = findViewById(R.id.edt_receptor_gain);
        EditTextChangeListener edtRGain_Listener = new EditTextChangeListener(edtReceptorGain, EditTextChangeListener.EDT_RECEPTOR_GAIN);

        EditText edtReceptorLoss = findViewById(R.id.edt_receptor_loss);
        EditTextChangeListener edtRLoss_Listener = new EditTextChangeListener(edtReceptorLoss, EditTextChangeListener.EDT_RECEPTOR_LOSS);

        EditText edtReceptorLm = findViewById(R.id.edt_receptor_lm);
        EditTextChangeListener edtRLm_Listener = new EditTextChangeListener(edtReceptorLm, EditTextChangeListener.EDT_RECEPTOR_LM);

    }

    private void calcLinkBudget(){
        if ((propagationD == 0) || (propagationF == 0)) {
            FSPL = 0;
        } else {
            FSPL = (20 * Math.log10(propagationD)) + (20 * Math.log10(propagationF)) + 32.44;
        }
        txtPropagationFSL.setText(String.format("FSL (dB) = %.2f", FSPL));

        lBudget = transmitterPo + transmitterGain - transmitterLoss - FSPL - receptorLm
                    + receptorGain - receptorLoss;
        Log.d("calcLinkBudget", String.format("El valor de LBudget = %.2f", lBudget));

        BigDecimal bigDecimal = new BigDecimal(String.valueOf(lBudget));
        if (lBudget < -140) {
            currentValue = -140;
        } else if (lBudget > -50) {
            currentValue = -50;
        } else {
            currentValue = bigDecimal.intValue();
        }

        gauge.moveToValue(currentValue);
        gauge.setLowerText(String.format("dBm = %d", bigDecimal.intValue()));
    }

    private class EditTextChangeListener{
        EditText editText;
        public static final int EDT_TRANSMITTER_P0 = 10;
        public static final int EDT_TRANSMITTER_GAIN = 11;
        public static final int EDT_TRANSMITTER_LOSS = 12;

        public static final int EDT_PROPAGATION_D = 20;
        public static final int EDT_PROPAGATION_F = 21;

        public static final int EDT_RECEPTOR_GAIN = 30;
        public static final int EDT_RECEPTOR_LOSS = 31;
        public static final int EDT_RECEPTOR_LM = 32;

        public EditTextChangeListener(EditText editText, final int codigoEditor){
            this.editText = editText;
            this.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.equals("")){
                        switch (codigoEditor){
                            case EDT_TRANSMITTER_P0: transmitterPo = 0; break;
                            case EDT_TRANSMITTER_GAIN: transmitterGain = 0; break;
                            case EDT_TRANSMITTER_LOSS: transmitterLoss = 0; break;
                            case EDT_PROPAGATION_D: propagationD = 0; break;
                            case EDT_PROPAGATION_F: propagationF = 0; break;
                            case EDT_RECEPTOR_GAIN: receptorGain = 0; break;
                            case EDT_RECEPTOR_LOSS: receptorLoss = 0; break;
                            case EDT_RECEPTOR_LM: receptorLm = 0; break;
                        }
                    } else {
                        BigDecimal valor;
                        try {
                            valor = new BigDecimal(s.toString());
                        } catch (Exception e){
                            valor = new BigDecimal("0");
                        }
                        switch (codigoEditor){
                            case EDT_TRANSMITTER_P0:
                                transmitterPo = valor.toBigInteger().intValue();
                                break;
                            case EDT_TRANSMITTER_GAIN:
                                transmitterGain = valor.toBigInteger().intValue();
                                break;
                            case EDT_TRANSMITTER_LOSS:
                                transmitterLoss = valor.toBigInteger().intValue();
                                break;
                            case EDT_PROPAGATION_D:
                                propagationD = valor.doubleValue();
                                break;
                            case EDT_PROPAGATION_F:
                                propagationF = valor.doubleValue();
                                break;
                            case EDT_RECEPTOR_GAIN:
                                receptorGain = valor.toBigInteger().intValue();
                                break;
                            case EDT_RECEPTOR_LOSS:
                                receptorLoss = valor.toBigInteger().intValue();
                                break;
                            case EDT_RECEPTOR_LM:
                                receptorLm = valor.toBigInteger().intValue();
                                break;
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    calcLinkBudget();
                }
            });
        }
    }
}
