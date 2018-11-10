package jlee.app.cal.wampfirebasemessaging;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment
{
    ImageView img;
    EditText age,name,race,religion,school;
    Button save;
    String image;

    public ProfileFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        img = v.findViewById(R.id.profilepic);
        age = v.findViewById(R.id.Age);
        name = v.findViewById(R.id.Name);
        race = v.findViewById(R.id.Race);
        religion = v.findViewById(R.id.Religion);
        school = v.findViewById(R.id.School);
        save = v.findViewById(R.id.Save);

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                saveProfile();
            }
        });

        img.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });
        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null )
        {
            //URI is the path to a file or image
            Uri selectedImage = data.getData();
            img.setImageURI(selectedImage);
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(HomeActivity.getContextOfApplication().getContentResolver(), selectedImage);
                image = getStringImage(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    //encodes the image as a string to be sent
    public String getStringImage(Bitmap bmp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void saveProfile()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, IPAddress.URL_SAVE_PROFILE,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        JSONObject obj = null;
                        try
                        {
                            obj = new JSONObject(response);
                            if (!obj.getBoolean("error"))
                            {
                                Toast.makeText(getContext(),"Saved",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getContext(),"Error Occured",Toast.LENGTH_LONG).show();
                    }
                })
        {
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("image", image);
                params.put("age", age.getText().toString());
                params.put("name", name.getText().toString());
                params.put("race", race.getText().toString());
                params.put("religion", religion.getText().toString());
                params.put("school", school.getText().toString());
                return params;
            }
        };
        MyVolley.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}
