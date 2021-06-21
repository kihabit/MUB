package com.prayosof.yvideo.view.fragment.help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.prayosof.yvideo.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FeedBackFragment extends Fragment {
    Button btnSubmit;
    EditText etFeedBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_feed_back, container, false);
        btnSubmit= (Button) view.findViewById(R.id.btnSubmit);
        etFeedBack = (EditText) view.findViewById(R.id.etFeedback);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(etFeedBack.getText().toString());
            }
        });
        return view;
    }
    private void sendEmail(String feedBack)
    {
        Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
        emailSelectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"djpappuvideo@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, feedBack);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        emailIntent.setSelector( emailSelectorIntent );

//        Uri attachment = FileProvider.getUriForFile(this, "my_fileprovider", myFile);
//        emailIntent.putExtra(Intent.EXTRA_STREAM, attachment);

        if( emailIntent.resolveActivity(getActivity().getPackageManager()) != null )
            startActivity(emailIntent);
    }
}