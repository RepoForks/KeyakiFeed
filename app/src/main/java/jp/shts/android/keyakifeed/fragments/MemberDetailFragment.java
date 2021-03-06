package jp.shts.android.keyakifeed.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.keyakifeed.R;
import jp.shts.android.keyakifeed.api.KeyakiFeedApiClient;
import jp.shts.android.keyakifeed.databinding.FragmentDetailMemberBinding;
import jp.shts.android.keyakifeed.models.Member;
import jp.shts.android.keyakifeed.providers.FavoriteContentObserver;
import jp.shts.android.keyakifeed.providers.dao.Favorites;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MemberDetailFragment extends Fragment {

    private static final String TAG = MemberDetailFragment.class.getSimpleName();

    public static MemberDetailFragment newInstance(Member member) {
        MemberDetailFragment memberDetailFragment = new MemberDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("member", member);
        memberDetailFragment.setArguments(bundle);
        return memberDetailFragment;
    }

    public static MemberDetailFragment newInstance(int memberId) {
        MemberDetailFragment memberDetailFragment = new MemberDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("memberId", memberId);
        memberDetailFragment.setArguments(bundle);
        return memberDetailFragment;
    }

    private FragmentDetailMemberBinding binding;
    private int maxScrollSize;
    private boolean isAvatarShown;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    private final FavoriteContentObserver favoriteContentObserver = new FavoriteContentObserver() {
        @Override
        public void onChangeState(@State int state) {
            switch (state) {
                case State.ADD:
                    Snackbar.make(binding.coordinator, "推しメン登録しました", Snackbar.LENGTH_SHORT).show();
                    break;
                case State.REMOVE:
                    Snackbar.make(binding.coordinator, "推しメン登録を解除しました", Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriteContentObserver.register(getContext());
    }

    @Override
    public void onDestroy() {
        favoriteContentObserver.unregister(getContext());
        subscriptions.unsubscribe();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_member, container, false);
        final Member member = getArguments().getParcelable("member");
        if (member == null) {
            subscriptions.add(KeyakiFeedApiClient.getMember(getArguments().getInt("memberId"))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(new Func1<Throwable, Member>() {
                        @Override
                        public Member call(Throwable e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .subscribe(new Action1<Member>() {
                        @Override
                        public void call(Member member) {
                            if (member == null) return;
                            setupComponents(member);
                        }
                    })
            );
            return binding.getRoot();
        }
        setupComponents(member);
        return binding.getRoot();
    }

    private void setupComponents(final Member member) {

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favorites.toggle(getContext(), member);
            }
        });

        binding.collapsingToolbar.setCollapsedTitleTextColor(
                ContextCompat.getColor(getContext(), android.R.color.white));
        binding.collapsingToolbar.setExpandedTitleColor(
                ContextCompat.getColor(getContext(), android.R.color.transparent));

        ViewPageAdapter adapter = new ViewPageAdapter(
                getActivity().getSupportFragmentManager(), member);
        binding.viewpager.setAdapter(adapter);
        binding.tabs.setupWithViewPager(binding.viewpager);

        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (maxScrollSize == 0)
                    maxScrollSize = appBarLayout.getTotalScrollRange();

                int percentage = (Math.abs(verticalOffset)) * 100 / maxScrollSize;

                if (percentage >= 20 && isAvatarShown) {
                    isAvatarShown = false;
                    binding.viewMemberDetailHeader.hideAnimation();
                }

                if (percentage <= 20 && !isAvatarShown) {
                    isAvatarShown = true;
                    binding.viewMemberDetailHeader.showAnimation();
                }
            }
        });
        maxScrollSize = binding.appBar.getTotalScrollRange();

        binding.viewMemberDetailHeader.setup(member);
        binding.collapsingToolbar.setTitle(member.getNameMain());
    }

    private static class ViewPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();

        public ViewPageAdapter(FragmentManager fm, Member member) {
            super(fm);
            MemberEntriesFragment memberEntriesFragment
                    = MemberEntriesFragment.newInstance(member);
            MemberImageGridFragment memberImageGridFragment
                    = MemberImageGridFragment.newInstance(member);
            fragments.add(memberEntriesFragment);
            fragments.add(memberImageGridFragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "ブログ";
            } else if (position == 1) {
                return "画像";
            }
            return super.getPageTitle(position);
        }
    }
}
