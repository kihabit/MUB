package com.prayosof.yvideo.view.browser.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.databinding.FragmentHome2Binding;
import com.prayosof.yvideo.helper.Constants;
import com.prayosof.yvideo.view.browser.models.SearchViewModel;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import static com.prayosof.yvideo.view.browser.fragments.ContentFragment.url;


public class HomeFragment extends Fragment implements View.OnClickListener, MaterialSearchBar.OnSearchActionListener, Observer {

    private SearchViewModel searchViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    FragmentHome2Binding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home2, container, false);

        openFragment(Constants.BROWSER, url);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setonClick();
        searchViewModel = new SearchViewModel(getActivity());
        setviewModel(searchViewModel);

    }

    private void setviewModel(Observable observable) {
        observable.addObserver(this);
    }

    private void setonClick() {
        binding.btnSearch.setOnClickListener(this);
        binding.btnDownload.setOnClickListener(this);
        binding.etUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    binding.btnSearch.performClick();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                openFragment(Constants.BROWSER, binding.etUrl.getText().toString().trim());
                break;
            case R.id.btn_download:
                if (getActivity() != null)
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new SaveFileFragment(), "webview").addToBackStack("webview").commit();
                break;
            default:
                break;
        }
    }

    private void openFragment(int flag, CharSequence text) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.FLAG, flag);
        bundle.putString(Constants.WORDS, text.toString());
        WebViewFragment webViewFragment = new WebViewFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, webViewFragment, "webview").addToBackStack("webview").commit();
        webViewFragment.setArguments(bundle);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}