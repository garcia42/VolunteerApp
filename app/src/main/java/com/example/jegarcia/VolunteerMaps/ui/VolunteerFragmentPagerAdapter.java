package com.example.jegarcia.VolunteerMaps.ui;

//public class VolunteerFragmentPagerAdapter extends FragmentPagerAdapter {
//
//    private SparseArray<Fragment> fragments = new SparseArray<>();
//
//    VolunteerFragmentPagerAdapter(FragmentManager fm) {
//        super(fm);
//    }
//
//    @Override
//    public Fragment getItem(int i) {
//
//        if (fragments.get(i) != null) {
//            return fragments.get(i);
//        }
//
//        Fragment fragment;
//        Bundle args = new Bundle();
//        switch (i) {
//            case 0:
//                fragment = new RecyclerViewFragment();
//                volunteerListFragment = (RecyclerViewFragment) fragment;
//                args.putString(LOCATION, city); // This needs current city
//                fragment.setArguments(args);
//                fragments.setValueAt(i, fragment);
//                return fragment;
//            case 1:
//                fragment = new Map();
//                //                    args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
//                fragment.setArguments(args);
//                fragments.setValueAt(i, fragment);
//                return fragment;
//        }
//        return null;
//    }
//
//    public Fragment getFragment(int position) {
//        return fragments.get(position);
//    }
//
//    @Override
//    public int getCount() {
//        return 2;
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return position == 0 ? "List" : "Map";
//    }
//}