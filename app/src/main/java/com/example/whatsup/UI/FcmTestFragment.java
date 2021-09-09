package com.example.whatsup.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsup.POJO.Classes.NotificationData;
import com.example.whatsup.POJO.Classes.PushedNotification;
import com.example.whatsup.POJO.Constants;
import com.example.whatsup.POJO.FCM.Client;
import com.example.whatsup.R;
import com.example.whatsup.databinding.FragmentFcmTestBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FcmTestFragment extends Fragment {

    private FragmentFcmTestBinding binding;

    public FcmTestFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFcmTestBinding.inflate(inflater);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                binding.token.setText(task.getResult());
            }
        });

        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = binding.title.getText().toString();
                String message = binding.message.getText().toString();
                String token = binding.token.getText().toString();
                if (title.isEmpty() || message.isEmpty() || token.isEmpty())
                    return;
//                pushNotification(new PushedNotification(new NotificationData(title, message,Constants.IMG_URL_TEMP), token));
            }
        });

        return binding.getRoot();
    }

    private void pushNotification(PushedNotification notification) {
        try{
            Client
                    .getApiInstance()
                    .pushNotification(notification)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}