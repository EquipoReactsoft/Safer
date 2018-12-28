package rs.com.safer.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.lang.annotation.Target;

import rs.com.safer.R;

public class ShareFragment extends Fragment {

    CardView cardShareFacebook;
    CallbackManager callbackManager;
    ShareDialog shareDialog;


    public ShareFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Compartenos");
        cardShareFacebook = getView().findViewById(R.id.cardShareFacebook);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        cardShareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setQuote("Safer App")
                        .setContentUrl(Uri.parse("https://www.facebook.com/ISUSHIPERU/?__xts__[0]=68.ARBUmj27lJaHH42hb4OTnp4k4SJQ8O0j_8COOAoFWC1RJYKDayt8qisCRa_69dKBWjHAjUr7AziULKMgGgWsGgp5qZhrsp__e3l_7iQhp8do9xW0FaLrzmLkpg_oGX9TpCzpIfkriYNySTmy1x5mhyi3KgiipdaSfu1tM4yQ6P7FtmZ7yScibr1gVQcXhTrbLvYRCwKlxBL19mc1o_edm8Fbkw"))
                        .build();

                if(ShareDialog.canShow(ShareLinkContent.class)){
                    shareDialog.show(linkContent);
                }

            }
        });


    }

}
