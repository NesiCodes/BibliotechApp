package com.dataflair.librarymanagementapp.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dataflair.librarymanagementapp.Activities.AdminActivity;
import com.dataflair.librarymanagementapp.MainActivity;
import com.dataflair.librarymanagementapp.Model.Model;
import com.dataflair.librarymanagementapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class AdminDashBoardAdapter extends FirebaseRecyclerAdapter<Model, AdminDashBoardAdapter.Viewholder> {
    private String sPhone;
    private String sMessage;
    private static String sPhone2;
    private static String sMessage2;
    private static String phoneNumberForSms;
    private static String messageForSms;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText arsyeRefuzimiText;
    private Button arsyeDergo;
    private Button arsyeAnullo;


    public String getsPhone() {
        return sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    public String getsMessage() {
        return sMessage;
    }

    public void setsMessage(String sMessage) {
        this.sMessage = sMessage;
    }

    public AdminDashBoardAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdminDashBoardAdapter.Viewholder holder, int position, @NonNull Model model) {

        holder.bookName.setText("Emri: " + model.getBookName());
        holder.booksCount.setText("Sasia: " + model.getBooksCount());
        holder.bookLocation.setText("Vendndodhja: " + model.getBookLocation());
        holder.bookAuthor.setText("Autori: " + model.getBookAuthor());

        holder.userNameTxt.setText(model.getName());
        holder.userPhoneNumberTxt.setText("Numri: " + model.getPhoneNumber());
        holder.userAddressTxt.setText("Adresa: " + model.getAddress());
        holder.userCityTxt.setText("Qyteti: " + model.getCity());
        holder.userEmailTxt.setText(model.getMail());

        Picasso.get().load(model.getImageUrl()).into(holder.imageView);
        FirebaseDatabase.getInstance().getReference().child("AllUsers").child(model.getUserId().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileChangedCountFromFireBase = snapshot.child("profileChangedCount").getValue().toString();
                int profileChangedCounts2 = Integer.parseInt(profileChangedCountFromFireBase);
                if(profileChangedCounts2 == 0){
                    StorageReference defaultProfileRef = FirebaseStorage.getInstance().getReference().child("users/" + "/profile.jpg");
                    defaultProfileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.println("Got here admin dashboard 2");
                            Picasso.get().load(uri).into(holder.userProfileImage);
                        }
                    });
                }else{
                    StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/" + model.getUserId().trim() +"/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.println("Got here admin dashboard 3");
                            Picasso.get().load(uri).into(holder.userProfileImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumberForSms = model.getPhoneNumber();
                messageForSms = "Kërkesa juaj për librin " + model.getBookName() + " u refuzua nga Admininstratori";
                System.out.println("Admin Dashboard Print");
                if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    sendMessage();
                }else{
                    ActivityCompat.requestPermissions((Activity)view.getContext(),new String[]{Manifest.permission.SEND_SMS},80);
                }

                String userId=model.getUserId();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("OrderedBooks");
                reference.orderByChild("bookName").equalTo(model.getBookName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String key = ds.getKey();

                            reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference().child("myOrderedBooks").child(userId)
                                                .child(key).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            dialogBuilder = new AlertDialog.Builder(view.getContext());
                                                            View popUpView = LayoutInflater.from(view.getContext()).inflate(R.layout.popup, null);
                                                            arsyeRefuzimiText = (EditText) popUpView.findViewById(R.id.arsyeaFromEditText);
                                                            arsyeDergo = (Button) popUpView.findViewById(R.id.dergoBtn);
                                                            arsyeAnullo = (Button) popUpView.findViewById(R.id.anulloBtn);

                                                            dialogBuilder.setView(popUpView);
                                                            dialog = dialogBuilder.create();
                                                            dialog.show();

                                                            arsyeDergo.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View s) {
                                                                    String msgFromText = arsyeRefuzimiText.getText().toString();
                                                                    sPhone = model.getPhoneNumber();
                                                                    sMessage = "Kërkesa juaj për librin " + model.getBookName() + " u refuzua nga Admininstratori";
                                                                    sPhone2 = sPhone;
                                                                    sMessage2 = sMessage;
                                                                    String data = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                                                    String finalMessage = sMessage2.concat("\nArsyeja: " + msgFromText + "\nData: " + data);
                                                                    sMessage2 = finalMessage;

                                                                    dialog.dismiss();

                                                                    String pushKey=FirebaseDatabase.getInstance().getReference().child("UserNotifications").push().getKey();

                                                                    FirebaseDatabase.getInstance().getReference().child("UserNotifications").child(userId).child(pushKey)
                                                                            .child("notification").setValue(sMessage2)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    //Showing the Toast message to the user
                                                                                    Toast.makeText(view.getContext(), "Kërkesa për librin u anullua me sukses", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            });

                                                            arsyeAnullo.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    dialog.dismiss();
                                                                }
                                                            });


                                                        }
                                                    }
                                                });
                                    }
                                }
                            });


                        }
                    }



                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


            }

        });



        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumberForSms = model.getPhoneNumber();
                messageForSms = "Kërkesa juaj për librin " + model.getBookName() + " u pranua nga Admininstratori dergesa do te behet me ane te postes ne adresen e dhene";
                System.out.println("Admin Dashboard Print");
                if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    sendMessage();
                }else{
                    ActivityCompat.requestPermissions((Activity)view.getContext(),new String[]{Manifest.permission.SEND_SMS},80);
                }
                String bookName = model.getBookName();
                String userName = model.getName();
                String userPhone = model.getPhoneNumber();
                String userEmail = model.getMail();
                String imageUrl = model.getImageUrl();
                String userId3 = model.getUserId();
                String givenDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                char c1 = givenDate.charAt(0);
                char c2 = givenDate.charAt(1);
                String str = new StringBuilder().append(c1).append(c2).toString();
                int num = Integer.parseInt(str) + 14;
                String returnDate = num + givenDate.substring(2);

                HashMap activeBookDetails=new HashMap();
                activeBookDetails.put("bookName", bookName);
                activeBookDetails.put("name", userName);
                activeBookDetails.put("phoneNumber", userPhone);
                activeBookDetails.put("mail", userEmail);
                activeBookDetails.put("givenDate", givenDate);
                activeBookDetails.put("returnDate", returnDate);
                activeBookDetails.put("imageUrl", imageUrl);
                activeBookDetails.put("userId", userId3);

                String push=FirebaseDatabase.getInstance().getReference().child("ActiveBooks").push().getKey();

                FirebaseDatabase.getInstance().getReference().child("ActiveBooks").child(push)
                        .updateChildren(activeBookDetails).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        phoneNumberForSms = model.getPhoneNumber();
                        messageForSms = "Kërkesa juaj për librin " + model.getBookName() + " u pranua nga Admininstratori";
                        if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                            sendMessage();
                        }else{
                            ActivityCompat.requestPermissions((Activity)view.getContext(),new String[]{Manifest.permission.SEND_SMS},80);
                        }
                        System.out.println("Me sukses");
                    }
                });


                String userId=model.getUserId();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("OrderedBooks");
                reference.orderByChild("bookName").equalTo(model.getBookName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String key = ds.getKey();
                            reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseDatabase.getInstance().getReference().child("myOrderedBooks").child(userId)
                                            .child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(view.getContext(), "Kërkesa për librin u pranua me sukses", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                String pushKey=FirebaseDatabase.getInstance().getReference().child("UserNotifications").push().getKey();

                FirebaseDatabase.getInstance().getReference().child("UserNotifications").child(model.getUserId())
                        .child(pushKey)
                        .child("notification").setValue("Kërkesa juaj për librin " + model.getBookName() + " u pranua nga Administratori")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(view.getContext(), "Libri u Pranua",Toast.LENGTH_SHORT).show();
                            }
                        });

            }


        });


    }

    public static void sendMessage(){
        //When permission is granted
        //Create method
        System.out.println("sendMessage printed");
        System.out.println(phoneNumberForSms);
        System.out.println(messageForSms);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumberForSms,null,messageForSms,null,null);
//        Toast.makeText(AdminActivity.this,"SMS u dergua me sukses",Toast.LENGTH_SHORT).show();
    }



    @NonNull
    @Override
    public AdminDashBoardAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_dashboard_layout_admin, parent, false);
        return new AdminDashBoardAdapter.Viewholder(view);
    }

    class Viewholder extends RecyclerView.ViewHolder {

        ImageView imageView, userProfileImage;
        TextView bookName, booksCount, bookLocation, bookAuthor;
        Button acceptBtn, cancleBtn;

        TextView userNameTxt, userAddressTxt, userPhoneNumberTxt, userCityTxt, userEmailTxt;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.BookImage);
            bookName = (TextView) itemView.findViewById(R.id.BookNameTxt);
            booksCount = (TextView) itemView.findViewById(R.id.BooksCountTxt);
            bookLocation = (TextView) itemView.findViewById(R.id.BooksLocationTxt);
            bookAuthor = (TextView) itemView.findViewById(R.id.bookAuthorEditTxt);

            userProfileImage = (ImageView) itemView.findViewById(R.id.profile_image_admin_dashboard);
            userNameTxt = (TextView) itemView.findViewById(R.id.UserNameTxt);
            userAddressTxt = (TextView) itemView.findViewById(R.id.UserAddressTxt);
            userPhoneNumberTxt = (TextView) itemView.findViewById(R.id.UserPhoneNumberTxt);
            userCityTxt = (TextView) itemView.findViewById(R.id.UserCityTxt);
            userEmailTxt = (TextView) itemView.findViewById(R.id.UserEmailTxt);

            acceptBtn=(Button)itemView.findViewById(R.id.AcceptBookBtn);
            cancleBtn=(Button)itemView.findViewById(R.id.CancleBookBtn);
        }
    }

}


