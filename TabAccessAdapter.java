package com.example.plproject;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;



public class TabAccessAdapter extends FragmentPagerAdapter {
    public TabAccessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                ChatFragment chatFragment=new ChatFragment();
                return chatFragment;
            case 1:
                AnnouncementsFragment announcementsFragment=new AnnouncementsFragment();
                return announcementsFragment;
            case 2:
                CoursesFragment coursesFragment=new CoursesFragment();
                return coursesFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "chats";
            case 1:
                return "announcements";
            case 2:
                return "courses";
            default:
                return null;
        }
    }
}
