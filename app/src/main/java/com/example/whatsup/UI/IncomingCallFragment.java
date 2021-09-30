package com.example.whatsup.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.whatsup.POJO.Classes.CallData;
import com.example.whatsup.POJO.Classes.PushedCall;
import com.example.whatsup.POJO.Constants;
import com.example.whatsup.POJO.FCM.Client;
import com.example.whatsup.POJO.WhatsUpUtils;
import com.example.whatsup.R;
import com.example.whatsup.databinding.FragmentIncomingCallBinding;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IncomingCallFragment extends Fragment {

    private FragmentIncomingCallBinding binding;
    private String type = null;


    public IncomingCallFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIncomingCallBinding.inflate(inflater);


        Bundle bundle = getArguments();

        String token = bundle.getString("sender_token");

        binding.username.setText(bundle.getString("sender_name"));
        binding.usernameAvatar.setText(String.valueOf(bundle.getString("sender_name").charAt(0)).toUpperCase());
        binding.phone.setText(bundle.getString("sender_number"));

        if (bundle.getString("call_type").equals("Voice Call"))
            binding.callType.setImageResource(R.drawable.ic_call);
        else
            binding.callType.setImageResource(R.drawable.ic_video);


        binding.cancelCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Client.getApiInstance().pushCall(new PushedCall(new
                        CallData(null, null, null, Constants.REJECT_STATE, null), token))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Call Canceled!!!", Toast.LENGTH_SHORT).show();
                                    Client.stopMediaPlayer();
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_incomingCallFragment_to_mainFragment);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
            }
        });

        binding.acceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bundle.getString("call_type").equals("Voice Call"))
                    type = Constants.VOICE_CALL;
                else
                    type = Constants.VIDEO_CALL;

                Client.getApiInstance().pushCall(new PushedCall(new
                        CallData(null, null, type, Constants.ACCEPT_STATE, null), token))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Call Accepted!!!", Toast.LENGTH_SHORT).show();
                                    Client.stopMediaPlayer();
                                    WhatsUpUtils.startCall(getContext(), type, token);
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_incomingCallFragment_to_mainFragment);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
            }
        });


        return binding.getRoot();
    }
}