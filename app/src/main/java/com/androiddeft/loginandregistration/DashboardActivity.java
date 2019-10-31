package com.androiddeft.loginandregistration;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestHandler;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SessionHandler session;
    private int PICK_EXCEL_REQUEST = 7;
    public static final String UPLOAD_KEY = "image";
    public static final String UPLOAD_IMAGES_URL ="http://192.168.43.21:8080/member/uploadimages.php";
    private Uri filePath;
    private Bitmap bitmap;
    private String username;
    public String UPLOAD_URL;
    public String UPLOAD_FILE;
    DrawerLayout drawer;
   private NavigationView navigationView;
    private  Toolbar toolbar;
    private static final int GalleryPick=1;
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    Button uploadTeachersData, uploaddata1,uploaddata2;
    CircleImageView userProfileImage,profileImageView;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        Button absentButton= (Button)findViewById(R.id.absent_button);
        uploadTeachersData=(Button) findViewById(R.id.upload_teachers_data);
        uploaddata1=(Button)findViewById(R.id.upload_files1);
        uploaddata2=(Button)findViewById(R.id.upload_files2);
        session = new SessionHandler(getApplicationContext());
        userProfileImage = (CircleImageView)  findViewById(R.id.user_profile_image);
        requestStoragePermission();
        final User user = session.getUserDetails();
        username= user.getUsername();
        Toast.makeText(this, "Welcome "+ user.getUsername()+", your session will expire on" + user.getSessionExpiryDate(), Toast.LENGTH_SHORT).show();
        absentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(DashboardActivity.this,AbsenteesList.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
        uploadTeachersData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UPLOAD_FILE="uploadTeachDetails.php";
                showFileChooser();
            }
        });
        uploaddata1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UPLOAD_FILE="uploadTimeTable.php";
                showFileChooser();
            }
        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_camera:
                        item.setChecked(true);
                        Toast.makeText(DashboardActivity.this, "Camera chosen", Toast.LENGTH_SHORT).show();
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_gallery:
                        item.setChecked(true);
                        Toast.makeText(DashboardActivity.this, "Gallery chosen", Toast.LENGTH_SHORT).show();
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_settings:
                        item.setChecked(true);
                        Toast.makeText(DashboardActivity.this, "Settings chosen", Toast.LENGTH_SHORT).show();
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_share:
                        item.setChecked(true);
                        Toast.makeText(DashboardActivity.this, "Sharing chosen", Toast.LENGTH_SHORT).show();
                        drawer.closeDrawers();
                        return true;

                    case R.id.send:
                        item.setChecked(true);
                        Toast.makeText(DashboardActivity.this, "Send option chosen", Toast.LENGTH_SHORT).show();
                        drawer.closeDrawers();
                        return true;
                    case R.id.logout:
                        item.setChecked(true);
                        session.logoutUser();
                        Intent i = new Intent(DashboardActivity.this,LoginActivity.class);
                        startActivity(i);
                        finish();
                        //drawer.closeDrawers();
                        return true;

                }
                return false;
            }
        });
        View headerView=navigationView.getHeaderView(0);

        profileImageView= (CircleImageView) headerView.findViewById(R.id.user_profile_image);
        TextView userNameTextView= headerView.findViewById(R.id.user_profile_name);
        userNameTextView.setText(username);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //galleryIntent.setAction(Intent.ACTION_VIEW);
                //galleryIntent.setType("images/*");
                startActivityForResult(galleryIntent,GalleryPick);
               // uploadImage();
            }
        });

    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select excel file"), PICK_EXCEL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_EXCEL_REQUEST) {
                filePath = data.getData();
                uploadFile();

            } else if (requestCode == GalleryPick){
                filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    profileImageView.setImageBitmap(bitmap);
                   uploadImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
        }

    private void uploadImage(){
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            ReqH rh = new ReqH();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DashboardActivity.this, "Uploading Image", "Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);

                String result = rh.sendPostRequest(UPLOAD_IMAGES_URL,data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void uploadFile(){
        //getting the actual path of the image
        String path=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            path = FilePath.getPath(this,filePath);
        if (path == null) {

            Toast.makeText(this, "Please move your .xls file to internal storage and retry", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"File uploaded successfully",Toast.LENGTH_SHORT).show();
            try {
                    UPLOAD_URL="http://192.168.43.21:8080/member/";
                    String uploadId = UUID.randomUUID().toString();
                    UPLOAD_URL=UPLOAD_URL+UPLOAD_FILE;
                    //Creating a multi part request
                    new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                            .addFileToUpload(path, "xls") //Adding file
                            .addParameter("username", username) //Adding text parameter to the request
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload(); //Starting the upload

                } catch (Exception exc) {
                    Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
        }
    }

}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


}

