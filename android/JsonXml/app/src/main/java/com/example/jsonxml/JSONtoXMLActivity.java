package com.example.jsonxml;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class JSONtoXMLActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsonto_xmlactivity);

        TextView greeting = (TextView)findViewById(R.id.greeting);
        greeting.setText("Welcome " + getIntent().getExtras().getString("name") + " !");

        Toast.makeText(this, "Welcome ! Populate either box to convert .", Toast.LENGTH_LONG).show();
    }

    public void convert (View v) {
        try {
            // Get the text from the JSON box and convert it to XML
            // or get the text from the XML box and convert it to JSON
            String xml = ((TextView)findViewById(R.id.xmlText)).getText().toString();
            String json = ((TextView)findViewById(R.id.jsonText)).getText().toString();

            // If the JSON box is empty, convert XML to JSON
            // If the XML box is empty, convert JSON to XML
            // If both boxes are empty, display a toast message saying "Please enter either JSON or XML"
            // If both boxes are filled, display a toast message saying "Please enter either JSON or XML"

            if (xml.equals("") && json.equals("")) {
                Toast.makeText(this, "Please enter either JSON or XML", Toast.LENGTH_LONG).show();
            }
            else if (xml.equals("")) {
                Toast.makeText(this, "JSON to XML", Toast.LENGTH_LONG).show();
                // Convert JSON to XML
                JSONObject jsonObject = new JSONObject(json);
                xml = XML.toString(jsonObject);
                ((TextView)findViewById(R.id.xmlText)).setText(xml);
            }
            else if (json.equals("")) {
                // Convert XML to JSON
                Toast.makeText(this, "XML to JSON", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Please enter either JSON or XML", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid JSON or XML", Toast.LENGTH_LONG).show();
        }
    }
}