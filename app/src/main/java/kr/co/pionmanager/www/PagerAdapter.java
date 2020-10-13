package kr.co.pionmanager.www;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import kr.co.pionmanager.www.Menu.MypageAboutData;

class PagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;

    PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MypageAboutData.newInstance();
            case 1:
                return AddAddressPage.newInstance();
            case 2:
                return RegistrationListPage.newInstance();
            case 3:
                return MenuCollection.newInstance();
            default:
                return null;
        }
    }

//    // Returns the page title for the top indicator
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return "Page " + position;
//    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }


}