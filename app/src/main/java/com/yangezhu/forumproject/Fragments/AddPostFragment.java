package com.yangezhu.forumproject.Fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yangezhu.forumproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AddPostFragment extends Fragment {

    private EditText edt_title;
    private EditText edt_description;

    private Spinner category_spinner;
    private Button btn_upload_images;
    private Button btn_post;

    private TextView txt_display_upload_images;

    ArrayList<Uri> uploaded_images_uri_list = new ArrayList<Uri>();

    String[] categories_list = {"Used Items", "Marketing", "Rent", "Used Cars"};
    private String selected_category;
    final int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        category_spinner = (Spinner) view.findViewById(R.id.category);
        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // On selecting a spinner item
                String item = adapterView.getItemAtPosition(i).toString();
                selected_category = item;
                // Showing selected spinner item
                Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        List<String> categories_list = new ArrayList<String>();
        categories_list.add("Used Items");
        categories_list.add("Marketing");
        categories_list.add("Rent");
        categories_list.add("Used Cars");

        ArrayAdapter<String> categories_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories_list);

        // Drop down layout style - list view with radio button
        categories_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        category_spinner.setAdapter(categories_adapter);

        btn_upload_images = (Button) view.findViewById(R.id.btn_upload_images);
        btn_upload_images.setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
        });

        txt_display_upload_images = (TextView) view.findViewById(R.id.txt_display_upload_images);
        edt_title = (EditText) view.findViewById(R.id.title) ;
        edt_description = (EditText) view.findViewById(R.id.description) ;
        btn_post= (Button) view.findViewById(R.id.btn_add_post);
        
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){
                    String imagePath = data.getData().getPath();
                    Log.d("YZHU_IMAGE_SELECT", "One image --> " + imagePath);
                    Uri mImageUri=data.getData();
                    uploaded_images_uri_list.add(mImageUri);

                    String display_text = "";
                    for (int i = 0; i < uploaded_images_uri_list.size(); i++) {

                        display_text += uploaded_images_uri_list.get(i).toString() + "\n";
                        Log.d("YZHU_IMAGE_SELECT", "Multiple images --> " + imagePath);
                    }
                    txt_display_upload_images.setText(display_text);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

                        int count = mClipData.getItemCount();
                        for (int i = 0; i < count; i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);

                            String imagePath = item.toString();
                            Log.d("YZHU_IMAGE_SELECT", "Multiple images --> " + imagePath);
                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}