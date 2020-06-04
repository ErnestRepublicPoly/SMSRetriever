package sg.edu.rp.c346.smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentSMS1 extends Fragment {

    public FragmentSMS1() {
        // Required empty public constructor
    }

    TextView tvshowNumSMS;
    EditText etNum;
    Button btnRetrieveNumSMS, btnEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_m_s1, container, false);
        tvshowNumSMS = view.findViewById(R.id.textViewShowSMSNumber);
        btnRetrieveNumSMS = view.findViewById(R.id.buttonRetrieveNumberSMS);
        btnEmail = view.findViewById(R.id.buttonEmail);
        etNum = view.findViewById(R.id.editTextNumber);

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mlgplayerz123@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test Email");
                emailIntent.putExtra(Intent.EXTRA_TEXT, tvshowNumSMS.getText().toString());

                startActivity(emailIntent);
            }
        });

        btnRetrieveNumSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }

                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                ContentResolver cr = getActivity().getContentResolver();
                String a = "";
                if(etNum.getText().toString().isEmpty() == false) {
                    a = etNum.getText().toString();
                }
                String filter="type LIKE ? AND address LIKE ?";
                String[] filterArgs = {"1", "%" + a + "%"};

                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);

                String smsBody = "";

                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvshowNumSMS.setText(smsBody);
            }
        });
        return view;
    }
}
