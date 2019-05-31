package com.example.uploadactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button uploadBn,chooseBn;
    private EditText name;
    private ImageView imageView;
private  final  int IMG_REQUEST=1;
private Bitmap bitmap;
private String  uploadUrl="http://192.168.43.65:8080/pro/imageApi/ss.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        uploadBn=findViewById(R.id.upimg);
        chooseBn=findViewById(R.id.chimg);
        name=findViewById(R.id.name);
        imageView=findViewById(R.id.imageview);

        chooseBn.setOnClickListener(this);
        uploadBn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.chimg:

selectImage();
                break;
            case R.id.upimg:
                uploadImage();
                break;

        }
    }

    private void selectImage()
    {

        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);

      //  by selecting from gallery
//        Intent intent =new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent,IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {



        if (resultCode != RESULT_CANCELED) {

            if (resultCode == RESULT_OK && data != null) {
               bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
               name.setVisibility(View.VISIBLE);
            }
        }




        //selecting from gallery
//        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null){
//            Uri path = data.getData();
//            try {
//                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),path);
//                imageView.setImageBitmap(bitmap);
//                imageView.setVisibility(View.VISIBLE);
//                name.setVisibility(View.VISIBLE);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }

    private void uploadImage(){
        Toast.makeText(MainActivity.this, "bitmapString-- "+imageToString(bitmap), Toast.LENGTH_LONG).show();
        Log.d("BitmapResponce->",imageToString(bitmap));

        StringRequest stringRequest= new StringRequest(Request.Method.POST, uploadUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String responce= jsonObject.getString("responce");
                    Toast.makeText(MainActivity.this,responce,Toast.LENGTH_LONG).show();
                    imageView.setImageResource(0);
                    imageView.setVisibility(View.GONE);
                    name.setText("");
                    name.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parms = new HashMap<>();
                parms.put("name",name.getText().toString().trim());
                parms.put("image",imageToString(bitmap));

                return parms;
            }
        };
        MySingleton.getInstance(MainActivity.this).addRequestQue(stringRequest);
    }
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageByte= byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageByte,Base64.DEFAULT);
    }
}
