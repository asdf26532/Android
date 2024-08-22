package com.example;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private UserDao mUserDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        UserDatabase database = Room.databaseBuilder(getApplicationContext(), UserDatabase.class, "test_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        mUserDao = database.userDao();

        // 삽입
//        User user = new User();
//        user.setName("Lee");
//        user.setAge(30);
//        user.setPhoneNumber("1234");
//        mUserDao.setInsertUser(user);

        // 조회
        List<User> userList = mUserDao.getUserAll();
        for (int i = 0; i < userList.size(); i++) {
            Log.d("TEST", userList.get(i).getName() + userList.get(i).getPhoneNumber());

        }

        // 수정
//        User user2 = new User();
//        user2.setId(1);
//        user2.setName("Kim");
//        user2.setAge(22);
//        user2.setPhoneNumber("5678");
//        mUserDao.setUpdateUser(user2);

        // 삭제
        User user3 = new User();
        user3.setId(2);
        mUserDao.setDeleteUser(user3);


    }
}