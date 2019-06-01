package com.jeff_jeong.gesturefeature;


// Created by Jeff_Jeong on 2019. 6. 1.


import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jeff_jeong.gesturefeature.models.Product;
import com.jeff_jeong.gesturefeature.touchhelpers.ItemTouchHelperAdapter;
import com.jeff_jeong.gesturefeature.util.BigDecimalUtil;
import com.jeff_jeong.gesturefeature.util.CartManager;

import java.util.ArrayList;

// 장바구니 리사이클러뷰 어답터
public class CartRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ItemTouchHelperAdapter,
        GestureDetector.OnGestureListener
{

    private static final String TAG = "CartRecyclerViewAd";

    private static final int PRODUCT_TYPE = 1;
    private static final int HEADER_TYPE = 2;

    //vars
    private ArrayList<Product> mProducts = new ArrayList<>();
    private Context mContext;
    private ItemTouchHelper mTouchHelper;
    private GestureDetector mGestureDetector;
    private ViewHolder mSelectedHolder;

    // 생성자 메소드
    public CartRecyclerViewAdapter(Context context, ArrayList<Product> products) {
        mContext = context;
        mProducts = products;
        //
        mGestureDetector = new GestureDetector(mContext, this);
    }

    // 진동을 주는 메소드
    private void makeSomeVibrate(){
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(50);
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        // 뷰의 타입에 따라 다른 레이아웃 보여준다.
        switch (viewType){

            // 제품 타입
            case PRODUCT_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_cart_list_item, parent, false);
                return new ViewHolder(view);

                // 해더타입
            case HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_cart_section_header, parent, false);
                return new SectionHeaderViewHolder(view);

            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_cart_list_item, parent, false);
                return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        // 뷰타입을 가져온다.
        int itemViewType = getItemViewType(position);
        // 뷰타입에 따라 분기처리
        if(itemViewType == PRODUCT_TYPE){
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);

            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(mProducts.get(position).getImage())
                    .into(((ViewHolder)holder).image);

            ((ViewHolder)holder).title.setText(mProducts.get(position).getTitle());
            ((ViewHolder)holder).price.setText(BigDecimalUtil.getValue(mProducts.get(position).getPrice()));

            // 뷰 홀더에 언 터치 리스너를 설정한다.
            ((ViewHolder)holder).parentView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        // 스크롤링 설정
                        ((ViewCartActivity)mContext).setIsScrolling(false);
                        Log.d(TAG, "onTouch: event.getAction() == MotionEvent.ACTION_DOWN" );
                        mSelectedHolder = ((ViewHolder)holder);
                        mGestureDetector.onTouchEvent(event);
                    }

                    return true;
                }
            });
        }
        else { // 해더 타입이면
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) holder;
            headerViewHolder.sectionTitle.setText(mProducts.get(position).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    // 아이템 뷰타입을 가져오는 메소드
    // 해당 메소드를 통해서 뷰 타입이 결정되고 onCreateView 홀더 메소드에 viewType 매개변수가 들어가게 된다.
    @Override
    public int getItemViewType(int position) {
        if(TextUtils.isEmpty(mProducts.get(position).getType())){
            return HEADER_TYPE;
        }
        else{
            return PRODUCT_TYPE;
        }
    }

    // 아이템이 이동되었을때
    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        // 드래그 시작점 아이템
        Product fromProduct = mProducts.get(fromPosition);
        Product product = new Product(fromProduct);
        // 시작 위치의 아이템을 지우고
        mProducts.remove(fromPosition);
        // 옮기는 위치에 기존 아이템을 넣는다.
        mProducts.add(toPosition, product);
        // 어답터에 데이터가 변경되었다고 알려준다.
        notifyItemMoved(fromPosition, toPosition);
    }

    // 아이템이 스와이프 되었을때
    @Override
    public void onItemSwiped(int position) {
        // 쉐어드에서 해당 아이템을 지운다.
        CartManager cartManager = new CartManager(mContext);
        cartManager.removeItemFromCart(mProducts.get(position));
        mProducts.remove(position);
        notifyItemRemoved(position);

    }

    // 아이템 터치 헬퍼를 설정하는 메소드
    public void setTouchHelper(ItemTouchHelper touchHelper){
        mTouchHelper = touchHelper;
    }


    // * OnGestureListener

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    // 아이템을 길게 눌렀을때
    @Override
    public void onLongPress(MotionEvent e) {

        Log.d(TAG, "onLongPress: ");

        // 사용자가 스크롤링 하는지 여부 체크
        if(!((ViewCartActivity)mContext).isScrolling()){
            // 진동을 준다.
//            makeSomeVibrate();
            // 드래그를 시작한다.
            mTouchHelper.startDrag(mSelectedHolder);
        }
        

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    // 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, price;
        RelativeLayout parentView;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            parentView = itemView.findViewById(R.id.parent);
        }
    }

    // 섹션 해더 뷰홀더 클래스
    public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {

        TextView sectionTitle;

        public SectionHeaderViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.cart_section_header);
        }
    }

}
