package com.udoolleh;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardWrite extends AppCompatActivity {
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    Button board_write_close, boardWrite_Posting, boardWrite_Image;
    EditText boardWrite_Title, boardWrite_Hashtag, boardWrite_Context;
    ImageView board_write_image;
    Context context;
    String name = "image";
    Uri URI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_board_write);

        context = BoardWrite.this;
        //뒤로가기 버튼
        board_write_close = findViewById(R.id.board_write_close);
        board_write_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        boardWrite_Title = findViewById(R.id.boardWrite_Title);
        boardWrite_Hashtag = findViewById(R.id.boardWrite_Hashtag);
        boardWrite_Context = findViewById(R.id.boardWrite_Context);
        boardWrite_Posting = findViewById(R.id.boardWrite_Posting);
        boardWrite_Image = findViewById(R.id.boardWrite_Image);
        board_write_image = findViewById(R.id.board_write_image);

        //이미지 선택
        boardWrite_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        //게시 버튼
        boardWrite_Posting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();

                String title = boardWrite_Title.getText().toString();
                String hashtag = boardWrite_Hashtag.getText().toString();
                String context = boardWrite_Context.getText().toString();

                //내용 미입력 시
                if (title.trim().length() == 0 || hashtag.trim().length() == 0 || context.trim().length() == 0 || title == null || hashtag == null || context == null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(BoardWrite.this);
                    builder.setTitle("알림")
                            .setMessage("내용을 입력하세요.")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    //게시판 등록 통신
                    BoardWriteResponse();
                }
            }
        });
    }

    //갤러리에서 이미지 골라 표시
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uriResource = data.getData();
                    ContentResolver resolver = getContentResolver();
                    try {
                        InputStream instream = resolver.openInputStream(uriResource);
                        Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                        board_write_image.setImageBitmap(imgBitmap);
                        instream.close();
                        URI = uriResource;
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "사진을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "실패", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //Uri to Multipart
    public static MultipartBody.Part uriToMultipart(final Uri uri, String name, final ContentResolver contentResolver) {
        final Cursor c = contentResolver.query(uri, null, null, null, null);
        if (c != null) {
            if(c.moveToNext()) {
                @SuppressLint("Range") final String displayName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                RequestBody requestBody = new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse(contentResolver.getType(uri));
                    }

                    @Override
                    public void writeTo(BufferedSink sink) {
                        try {
                            sink.writeAll(Okio.source(contentResolver.openInputStream(uri)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                c.close();
                return MultipartBody.Part.createFormData(name, displayName, requestBody);
            } else {
                c.close();
                return null;
            }
        } else {
            return null;
        }
    }

    //게시글 등록 레트로핏 통신
    public void BoardWriteResponse() {
        //토큰 가져오기
        SharedPreferences sp = getSharedPreferences("DATA_STORE", MODE_PRIVATE);
        String accToken = sp.getString("accToken", "");

        //file
        MultipartBody.Part filePart;
        if (board_write_image.getDrawable() != null) {
            filePart = uriToMultipart(URI, name, context.getContentResolver());
        } else {
            filePart = null;
        }

        //requestDto
        String title = boardWrite_Title.getText().toString().trim();
        String hashtag = boardWrite_Hashtag.getText().toString().trim();
        String context = boardWrite_Context.getText().toString().trim();
        RequestBody requestDto = RequestBody.create(MediaType.parse("application/json"), "{\"title\": \"" + title + "\", \"hashtag\": \"" + hashtag + "\", \"context\": \"" + context + "\"}");
        Log.d("udoLog", "게시판 작성 requestDto {\"title\": \"" + title + "\", \"hashtag\": \"" + hashtag + "\", \"context\": \"" + context + "\"}");

        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance(accToken);
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        //accToken, files, map을 RequestParams와 RequestBody로 저장 후 getBoardWriteRespons로 함수를 실행한 후 응답을 받음
        retrofitInterface.getBoardWriteResponse(filePart, requestDto).enqueue(new Callback<BoardWriteResponse>() {
            @Override
            public void onResponse(Call<BoardWriteResponse> call, Response<BoardWriteResponse> response) {
                Log.d("udoLog", "게시판 작성 Data fetch success");
                Log.d("udoLog", "게시판 작성 body 내용" + response.body());
                Log.d("udoLog", "게시판 작성 성공여부" + response.isSuccessful());
                Log.d("udoLog", "게시판 작성 상태코드" + response.code());

                //통신 성공
                if (response.isSuccessful() && response.body() != null) {

                    //response.body()를 result에 저장
                    BoardWriteResponse result = response.body();

                    //받은 코드 저장
                    int resultCode = response.code();

                    int success = 200; //게시 성공
                    int errPm = 400; //파라미터 유효x
                    int errTk = 403; //토큰 에러, 사용자 에러

                    if (resultCode == success) {
                        Toast.makeText(BoardWrite.this, "게시물을 등록하였습니다.", Toast.LENGTH_LONG).show();
                    } else if (resultCode == errPm) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BoardWrite.this);
                        builder.setTitle("알림")
                                .setMessage("errPm예기치 못한 오류가 발생하였습니다.\n 잠시후 다시 시도해주세요.")
                                .setPositiveButton("확인", null)
                                .create()
                                .show();
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else if (resultCode == errTk) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BoardWrite.this);
                        builder.setTitle("알림")
                                .setMessage("로그인 정보가 유효하지 않습니다.\n 로그인 상태를 확인해주세요.")
                                .setPositiveButton("확인", null)
                                .create()
                                .show();
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BoardWrite.this);
                        builder.setTitle("알림")
                                .setMessage("errElse예기치 못한 오류가 발생하였습니다.\n 고객센터에 문의바랍니다.")
                                .setPositiveButton("확인", null)
                                .create()
                                .show();
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BoardWriteResponse> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BoardWrite.this);
                builder.setTitle("알림")
                        .setMessage("errFail예기치 못한 오류가 발생하였습니다.\n 고객센터에 문의바랍니다.")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }
        });
    }

    //키보드 숨기기
    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(boardWrite_Title.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(boardWrite_Hashtag.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(boardWrite_Context.getWindowToken(), 0);
    }

    //화면 터치 시 키보드 내려감
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
