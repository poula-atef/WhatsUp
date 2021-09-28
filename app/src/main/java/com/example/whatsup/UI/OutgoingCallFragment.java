package com.example.whatsup.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whatsup.POJO.Classes.CallData;
import com.example.whatsup.POJO.Classes.PushedCall;
import com.example.whatsup.POJO.Constants;
import com.example.whatsup.POJO.FCM.Client;
import com.example.whatsup.R;
import com.example.whatsup.databinding.FragmentOutGoingCallBinding;
import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingCallFragment extends Fragment {

    private FragmentOutGoingCallBinding binding;

    public OutgoingCallFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOutGoingCallBinding.inflate(inflater);


        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC);

        Bundle bundle = getArguments();


        setCallData(bundle);


        binding.cancelCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Client.getApiInstance().pushCall(new PushedCall(new CallData(null, null, null, Constants.REJECT_STATE, null), bundle.getString("receiver_token"))).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Call Canceled!!!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_outgoingCallFragment_to_mainFragment);
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

    private void setCallData(Bundle bundle) {
        String token = bundle.getString("receiver_token");
        String callType = bundle.getString("call_type");
        String receiverName = bundle.getString("receiver_name");
        String receiverNumber = bundle.getString("receiver_number");
        String myToken = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.MY_TOKEN, "?");

        binding.username.setText(receiverName);
        binding.usernameAvatar.setText(String.valueOf(receiverName.charAt(0)).toUpperCase());
        binding.phone.setText(receiverNumber);
        if (callType.equals("Voice Call"))
            binding.callType.setImageResource(R.drawable.ic_call);
        else
            binding.callType.setImageResource(R.drawable.ic_video);
        if (bundle.getString("state") != null) {
            binding.state.setText("Ringing..");
            Client.setTimerForEndCall(token,binding.getRoot());
            return;
        }
        makeTheCall(receiverName, receiverNumber, callType, myToken, token);
    }


    private void makeTheCall(String receiverName, String receiverNumber, String callType, String myToken, String token) {

        PushedCall call = new PushedCall(new CallData(receiverName, receiverNumber, callType, null, myToken), token);
        Client.getApiInstance().pushCall(call)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            Toast.makeText(getContext(), "User Received Invitation!!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "response code is : " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        Client.stopTimerForEndCall();
    }
}