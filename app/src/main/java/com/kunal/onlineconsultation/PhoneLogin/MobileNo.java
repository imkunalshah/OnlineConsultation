package com.kunal.onlineconsultation.PhoneLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hbb20.CountryCodePicker;
import com.kunal.onlineconsultation.R;

public class MobileNo extends AppCompatActivity {
LinearLayout submit;
CountryCodePicker countryCodePicker;
EditText phone_no;
String phone,ph_no;
String country_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_no);

        submit = findViewById(R.id.submit);
        countryCodePicker= findViewById(R.id.ccp);
        phone_no = findViewById(R.id.phone_no);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                country_code = countryCodePicker.getSelectedCountryCodeWithPlus();
                phone = phone_no.getText().toString().trim();
                ph_no = country_code.concat(phone);

                if (phone.isEmpty()){
                    phone_no.setError("Please Enter Phone No.");
                    phone_no.requestFocus();
                    return;
                }
                else if (phone.length() != 10)
                {
                    phone_no.setError("Enter a Valid Phone Number");
                    phone_no.requestFocus();
                    return;
                }
                Intent intent = new Intent(MobileNo.this,VerifyOtp.class);
                intent.putExtra("phonenumber",ph_no);
                intent.putExtra("location",getIntent().getExtras().get("location").toString());
                startActivity(intent);
            }
        });
    }
}
