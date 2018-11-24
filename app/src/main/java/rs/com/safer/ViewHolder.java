package rs.com.safer;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;
    }

    public void setDetails(Context context, String title, String image, String user, String date){
        TextView mTitleCV = mView.findViewById(R.id.titleCardView);
        //TextView mDescriptionCV = mView.findViewById(R.id.descriptionCardView);
        ImageView mImageCV = mView.findViewById(R.id.imageCardView);
        TextView mUserCV = mView.findViewById(R.id.userCardView);
        TextView mDateCV = mView.findViewById(R.id.dateCardView);

        mTitleCV.setText(title);
        //mDescriptionCV.setText(description);
        Picasso.get().load(image).into(mImageCV);
        mUserCV.setText(user);
        mDateCV.setText(date);
    }
}
