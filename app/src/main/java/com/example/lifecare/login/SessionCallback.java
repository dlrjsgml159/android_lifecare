package com.example.lifecare.login;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.lifecare.EclipseConnect.SignInActivity;
import com.example.lifecare.MainActivity;
import com.example.lifecare.R;
import com.example.lifecare.VO.UserVO;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.muddzdev.styleabletoast.StyleableToast;

public class SessionCallback implements ISessionCallback {
    UserVO userVO = UserVO.getInstance();

    // 로그인에 성공한 상태
    @Override
    public void onSessionOpened() {
        requestMe();
    }

    // 로그인에 실패한 상태
    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
    }

    // 사용자 정보 요청
    public void requestMe() {
        UserManagement.getInstance()
                .me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        Log.i("KAKAO_API", "사용자 아이디: " + result.getId());


                        UserAccount kakaoAccount = result.getKakaoAccount();
                        if (kakaoAccount != null) {
                            userVO.setId(String.valueOf(result.getId()));

                            // 이메일
                            String email = kakaoAccount.getEmail();

                            if (email != null) {
                                Log.i("KAKAO_API", "email: " + email);

                            } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 이메일 획득 가능
                                // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.

                            } else {
                                // 이메일 획득 불가
                            }

                            // 프로필
                            Profile profile = kakaoAccount.getProfile();

                            if (profile != null) {

                                Log.d("KAKAO_API", "nickname: " + profile.getNickname());
                                Log.d("KAKAO_API", "profile image: " + profile.getProfileImageUrl());
                                Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());

                            } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 프로필 정보 획득 가능

                            } else {
                                // 프로필 획득 불가
                            }
                        }
                    }
                });
    }
}
