package com.dataflair.librarymanagementapp.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dataflair.librarymanagementapp.Model.Model;
import com.dataflair.librarymanagementapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActiveBooksAdapter extends FirebaseRecyclerAdapter<Model, ActiveBooksAdapter.Viewholder> {
    private static String phoneNumberForSms;
    private static String messageForSms;

    public ActiveBooksAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ActiveBooksAdapter.Viewholder holder, int position, @NonNull Model model) {
        holder.bookName.setText("Emri i librit: " + model.getBookName());
        holder.userName.setText("Emri i perdoruesit: " + model.getName());
        holder.userPhone.setText("Numri: " + model.getPhoneNumber());
        holder.userEmail.setText("Email: " + model.getMail());
        holder.givenDate.setText("Data e dhenies: " + model.getGivenDate());
        holder.returnDate.setText("Data e rikthimit: " + model.getReturnDate());

        Picasso.get().load(model.getImageUrl()).into(holder.imageView);

        holder.collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberForSms = model.getPhoneNumber();
                String returnDate = model.getReturnDate();
                char r1 = returnDate.charAt(0);
                char r2 = returnDate.charAt(1);
                String returnDateDay = new StringBuilder().append(r1).append(r2).toString();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                char c1 = currentDate.charAt(0);
                char c2 = currentDate.charAt(1);
                String currentDateDay = new StringBuilder().append(c1).append(c2).toString();
                int daysLeft = Integer.parseInt(returnDateDay) - Integer.parseInt(currentDateDay);
                messageForSms = "Përshëndetje " + model.getName() + ", ju njoftojme se keni edhe " + daysLeft + " ditë kohë për të rikthyer librin " + model.getBookName();
                if(ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    sendMessage();
                }else{
                    ActivityCompat.requestPermissions((Activity)v.getContext(),new String[]{Manifest.permission.SEND_SMS},60);
                }
                String pushKey= FirebaseDatabase.getInstance().getReference().child("UserNotifications").push().getKey();

                FirebaseDatabase.getInstance().getReference().child("UserNotifications").child(model.getUserId())
                        .child(pushKey)
                        .child("notification").setValue(messageForSms)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(v.getContext(), "Njoftimi u dërgua",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    public static void sendMessage(){
        System.out.println("sendMessage printed");
        System.out.println(phoneNumberForSms);
        System.out.println(messageForSms);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumberForSms,null,messageForSms,null,null);
    }

    @NonNull
    @Override
    public ActiveBooksAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_active_books_layout_admin, parent, false);
        return new ActiveBooksAdapter.Viewholder(view);
    }

    class Viewholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView bookName, userName, userPhone, userEmail, givenDate, returnDate;
        Button collectBtn;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.BookImage);
            bookName = (TextView) itemView.findViewById(R.id.BookNameTxt);
            userName = (TextView) itemView.findViewById(R.id.UserName);
            userPhone = (TextView) itemView.findViewById(R.id.UserPhone);
            userEmail = (TextView) itemView.findViewById(R.id.UserEmail);
            givenDate = (TextView) itemView.findViewById(R.id.GivenDate);
            returnDate = (TextView) itemView.findViewById(R.id.ReturnDate);

            collectBtn = (Button) itemView.findViewById(R.id.CollectBookBtn);

        }
    }

}
