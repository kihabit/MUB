package com.prayosof.yvideo.view.fragment.video;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.view.fragment.dashboard.VideosListFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.prayosof.yvideo.adapter.AllMediaListAdapter.videosModelList;
import static com.prayosof.yvideo.adapter.VideosListAdpter.mListner;
import static com.prayosof.yvideo.adapter.VideosListAdpter.videosListData;
import static com.prayosof.yvideo.view.fragment.dashboard.VideosListFragment.obj_adapter;
import static com.prayosof.yvideo.view.fragment.video.AllVideosListFragment.adapter;


public class MainVideoFragment extends Fragment {

    private View view;
    private TabLayout tabLayout;
    public static ImageView ivDelete;
    private ViewPager viewPager;

    public static String type = "";
    public static int count = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_video, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_main_videos);
        ivDelete = (ImageView) view.findViewById(R.id.iv_main_delete);
        viewPager = (ViewPager) view.findViewById(R.id.vp_main_videos);

        ivDelete.setClickable(false);
        ivDelete.setEnabled(false);
        ivDelete.setColorFilter(getResources().getColor(R.color.black));

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you really want to delete files?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type.equals("videos")) {
                            for (int i = 0; i < videosModelList.size(); i++) {
                                if (videosModelList.get(i).isSelected()) {
//                                    adapter.deleteFile(i, v);
                                    File file = new File(videosModelList.get(i).getFileName());
                                    file.delete();
                                    videosModelList.remove(i);
                                    adapter.notifyItemRemoved(i);
                                    adapter.notifyItemRangeChanged(i, videosModelList.size());
                                    i--;
                                }
                            }
                        } else if (type.equals("folder")) {
                            for (int i = 0; i < videosListData.size(); i++) {
                                if (videosListData.get(i).isSelected()) {
                                    mListner.onItemDelete(i);
                                }
                            }
                        }
                        count = 0;
                        ivDelete.setClickable(false);
                        ivDelete.setEnabled(false);
                        ivDelete.setColorFilter(getResources().getColor(R.color.black));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type.equals("videos")) {
                            for (int i = 0; i < videosModelList.size(); i++) {
                                videosModelList.get(i).setSelected(false);
                            }
                            adapter.updateList(videosModelList);
                        } else if (type.equals("folder")) {
                            for (int i = 0; i < videosListData.size(); i++) {
                                videosListData.get(i).setSelected(false);
                            }
                            obj_adapter.updateList(videosListData);
                        }
                        count = 0;
                        ivDelete.setClickable(false);
                        ivDelete.setEnabled(false);
                        ivDelete.setColorFilter(getResources().getColor(R.color.black));

                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (count <= 0) {
            ivDelete.setClickable(false);
            ivDelete.setEnabled(false);
            ivDelete.setColorFilter(getActivity().getResources().getColor(R.color.black));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new AllVideosListFragment(), "Videos");
        adapter.addFragment(new VideosListFragment(), "Folders");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}