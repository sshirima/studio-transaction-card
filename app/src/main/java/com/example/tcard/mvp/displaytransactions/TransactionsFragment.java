package com.example.tcard.mvp.displaytransactions;


import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tcard.mvp.utils.Transaction;
import com.example.transactioncard.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.tcard.mvp.Preconditions.checkNotNull;

/**
 * Created by sshirima on 9/19/16.
 */
public class TransactionsFragment extends Fragment implements DisplayTransContract.View{

    private DisplayTransContract.Presenter mPresenter;
    //Transactions Adapter
    private TransactionsAdapter mListAdapter;

    public TransactionsFragment(){
        //Requires emmpty public constructor
    }

    public static TransactionsFragment newInstance(){return new TransactionsFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TransactionsAdapter(getActivity(), new ArrayList<Object>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_displaytransactions, container, false);

        //Set up transactions view
        ListView listview = (ListView)root.findViewById(R.id.lvDisplayTransactions);
        listview.setAdapter(mListAdapter);
        return root;

    }

    @Override
    public void showTransactions(List<Transaction> transactions) {
        List<Object> objects = new ArrayList<Object>();
        //Create transaction list with Date, Time label

        mListAdapter.replaceData(objects);
    }

    @Override
    public void showNoTasks() {

    }

    @Override
    public void showAddTransaction() {
        //Open New transaction activity
    }

    @Override
    public void showSuccessfullySavedMessage() {

    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setPresenter(DisplayTransContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter,"TransactionsFragment#setPresenter:Presenter can not be null");
    }

    public static class TransactionsAdapter extends BaseAdapter{

        private List<Object> mObjects = new ArrayList<Object>();
        private LayoutInflater mInflater;

        private static final String  TXT_ON = " on ";
        private int mArrowDown =  R.drawable.ic_arrow_down;
        private int mArrowUp =  R.drawable.ic_arrow_up;
        String mExpense;
        String mIncome;

        public TransactionsAdapter(Context context, List<Object> objects){

            setObjects(objects);
            mInflater = LayoutInflater.from(context);
            mExpense = context.getResources().getString(R.string.text_expense);
            mIncome = context.getResources().getString(R.string.text_income);
        }

        public void replaceData(List<Object> objects){
            setObjects(objects);
            notifyDataSetChanged();
        }

        private void setObjects(List<Object> objects) {
            checkNotNull(objects, "TransactionsAdapter#setObjects: Object cannot be null");
            mObjects = objects;
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null){
                convertView = mInflater.inflate(R.layout.listview_home, null);
                holder = new ViewHolder();
                holder.tvAmount = (TextView) convertView
                        .findViewById(R.id.tvAmount);
                holder.tvCurrency = (TextView) convertView
                        .findViewById(R.id.tvCurrency);
                holder.tvDate = (TextView) convertView
                        .findViewById(R.id.tvTime);
                holder.tvDes = (TextView) convertView
                        .findViewById(R.id.tvDescription);
                holder.ivCategory = (ImageView) convertView
                        .findViewById(R.id.ivCategory);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //Binding data to the convertview

            Transaction transaction = (Transaction)mObjects.get(position);

            holder.tvAmount.setText(transaction.getAmount());
            holder.tvDate.setText(transaction.getDate()+TXT_ON+transaction.getTime());
            holder.tvCurrency.setText(transaction.getCurrencyCode());
            holder.tvDes.setText(transaction.getDescription().getName());
            if (transaction.getDescription().getCategory().equals(mExpense)){
                holder.ivCategory.setImageResource(mArrowUp);
            } else if (transaction.getDescription().getCategory().equals(mIncome)) {
                holder.ivCategory.setImageResource(mArrowDown);
            }
            return convertView;
        }

        static class ViewHolder {
            TextView tvAmount, tvDate, tvDes, tvCurrency,
                    tvSeparatorTotExpenses, tvSeparatorTotIncome;
            ImageView ivCategory;
        }
    }
}
