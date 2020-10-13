package kr.co.pionmanager.www;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class NetworkCheck extends ConnectivityManager.NetworkCallback {

    private static final String TAG = "NetworkCheck";
    private Context context;
    private NetworkRequest networkRequest;
    private ConnectivityManager connectivityManager;

    public NetworkCheck(Context context) {
        Log.e(TAG, "NetworkCheck: " );
        this.context = context;
        networkRequest =
                new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build();
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    /**
     * 콜백 연결
     */
    public void register() {
        Log.e(TAG, "register: " );
        this.connectivityManager.registerNetworkCallback(networkRequest, this);
    }


    /**
     * 콜백 끊기
     */
    public void unregister() {
        this.connectivityManager.unregisterNetworkCallback(this);
    }


    /**
     * 콜백을 연결했을 때 네트워크가 이용가능한 상태면
     *
     * @param network
     */
    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
//        Toast.makeText(this.context, "네트워크가 연결되었습니다", Toast.LENGTH_SHORT).show();
    }

    /**
     * 콜백을 연결했을 때 네트워크 끊기면 진입
     *
     * @param network
     */
    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Toast.makeText(this.context, "네트워크 연결이 해제되었습니다", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnavailable() {
        Toast.makeText(this.context, "네트워크를 확인해주세요", Toast.LENGTH_SHORT).show();
        super.onUnavailable();
    }

}
