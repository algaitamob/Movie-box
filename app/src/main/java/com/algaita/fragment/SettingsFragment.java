package com.algaita.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algaita.Config;
import com.algaita.R;
import com.algaita.RequestHandler;
import com.algaita.ViewDialog;
import com.algaita.activities.BaseActivity;
import com.algaita.activities.LoginActivity;
import com.algaita.sessions.SessionHandlerUser;

import java.util.HashMap;

public class SettingsFragment extends Fragment {
    private View view;
    private BaseActivity baseActivity;


    SessionHandlerUser session;
    ViewDialog viewDialog;

    private EditText et_oldpass, et_newpass;
    private Button btn_save, btn_deactivate;
    private String oldpass, newpass;
    private final static String KEY_EMPTY = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        viewDialog = new ViewDialog(getActivity());
        session = new SessionHandlerUser(getActivity());
        et_oldpass = view.findViewById(R.id.oldpass);
        et_newpass = view.findViewById(R.id.newpass);
        btn_save = view.findViewById(R.id.btn_save);
        btn_deactivate = view.findViewById(R.id.btn_deactivate);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldpass = et_oldpass.getText().toString().trim();
                newpass = et_newpass.getText().toString().trim();

                if (validateInputs()){
                    ChangePass(oldpass, newpass);
                }
            }
        });

        btn_deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.oldicon)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage("Are you sure you want to delete this account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                DeleteAccount();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });
        return view;
    }


    private void ChangePass(final String oldpass, final String newpass) {

        class change extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                viewDialog.showDialog();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                viewDialog.hideDialog();
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) view.findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText(s);
                Toast toast = new Toast(getActivity());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();

                data.put("oldpass", oldpass);
                data.put("userid", String.valueOf(session.getUserDetail().getUserid()));
                data.put("newpass", newpass);
                String result = rh.sendPostRequest(Config.url + "change_password.php",data);

                return result;
            }
        }

        change ui = new change();
        ui.execute();
    }



    private void DeleteAccount() {
        class regdevice extends AsyncTask<Bitmap,Void,String> {

            RequestHandler rh = new RequestHandler();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                viewDialog.hideDialog();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, view.findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText(s);
                Toast toast = new Toast(getContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                session.logoutUser();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();

                data.put("userid", String.valueOf(session.getUserDetail().getUserid()));
                String result = rh.sendPostRequest(Config.url + "delete_account.php",data);

                return result;
            }
        }

        regdevice ui = new regdevice();
        ui.execute();
    }


    /**
     * Validates inputs and shows error if any
     *
     * @return
     */
    private boolean validateInputs() {
        if (KEY_EMPTY.equals(oldpass)) {
            et_oldpass.setError("Old Password cannot be empty");
            et_oldpass.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(newpass)) {
            et_newpass.setError("New Password cannot be empty");
            et_newpass.requestFocus();
            return false;
        }
        return true;
    }


}
