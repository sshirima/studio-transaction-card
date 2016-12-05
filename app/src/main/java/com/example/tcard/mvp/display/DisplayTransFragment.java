package com.example.tcard.mvp.display;


import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.example.tcard.mvp.addedit.AddTransActivity;
import com.example.tcard.mvp.data.Transaction;
import com.example.transactioncard.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static com.example.tcard.mvp.Preconditions.checkNotNull;

/**
 * Created by sshirima on 9/19/16.
 */
public class DisplayTransFragment extends Fragment implements DisplayTransContract.View{

    private DisplayTransContract.Presenter mPresenter;
    //Transactions Adapter
    private TransactionsAdapter mListAdapter;

    public DisplayTransFragment(){
        //Requires empty public constructor
    }

    public static DisplayTransFragment newInstance(){return new DisplayTransFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TransactionsAdapter(getActivity(), new ArrayList<>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fabAddTransaction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewTransaction();
            }
        });
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
        //Create transaction list with Date, Time label
        mListAdapter.clearAdapter();
        mListAdapter.setTransactions(transactions);
        mListAdapter.putLabelOnTransactionList();
    }

    @Override
    public void showNoTasks() {

    }

    @Override
    public void showAddTransaction() {
        //Open New transaction activity
        Intent intent = new Intent(getContext(), AddTransActivity.class);
        startActivityForResult(intent, AddTransActivity.REQUEST_ADD_TRANSACTION);
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
        mPresenter = checkNotNull(presenter,"DisplayTransFragment#setPresenter:Presenter can not be null");
    }

    public static class TransactionsAdapter extends BaseAdapter{

        private List<Object> mObjects = new ArrayList<Object>();
        private List<Transaction> mTransactions = new ArrayList<Transaction>();
        private LayoutInflater mInflater;
        private TreeSet mSeparatorPos = new TreeSet();

        private static final int TYPE_TRANSACTION = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
        private static final String  TXT_ON = " on ";
        private int mArrowDown =  R.drawable.ic_arrow_down;
        private int mArrowUp =  R.drawable.ic_arrow_up;
        String mExpense;
        String mIncome;

        public TransactionsAdapter(Context context, List<Object> objects){

            setObjects(objects);
            mExpense = context.getResources().getString(R.string.text_expense);
            mIncome = context.getResources().getString(R.string.text_income);
            mInflater = LayoutInflater.from(context);
        }

        public void replaceData(List<Object> objects){
            setObjects(objects);
            notifyDataSetChanged();
        }

        private void setObjects(List<Object> objects) {
            checkNotNull(objects, "TransactionsAdapter#setObjects: Object cannot be null");
            this.mObjects = objects;
        }

        @Override
        public int getItemViewType(int position) {
            return mSeparatorPos.contains(position) ? TYPE_SEPARATOR : TYPE_TRANSACTION;
        }

        // clear adapter
        public void clearAdapter() {
            this.mObjects.clear();
            this.mSeparatorPos.clear();
            notifyDataSetChanged();
        }

        /**
         * Set and sort transactions
         * @param transactions
         */
        public void setTransactions(List<Transaction> transactions){
            ArrayList<Transaction> sortedTransactions = new ArrayList<Transaction>();
            ArrayList<Long> transactioTimeList = new ArrayList<Long>();
            for (int i = 0; i < transactions.size(); i++) {
                transactioTimeList.add(transactions.get(i).getTimeInMills());
            }
            //Sort transaction list by time, desc
            Collections.sort(transactioTimeList);
            Collections.reverse(transactioTimeList);
            for (long time : transactioTimeList) {
                for (int i = 0; i < transactions.size(); i++) {
                    if (time == transactions.get(i).getTimeInMills()) {
                        sortedTransactions.add(transactions.get(i));
                        break;
                    }
                }
            }
           mTransactions = sortedTransactions;
        }

        public void putLabelOnTransactionList(){
            for (int i =0;i< mTransactions.size(); i ++){
                if (i==0){
                    addSeparatorItem(mTransactions.get(i).getDate());
                    mObjects.add(mTransactions.get(i));
                }else{
                    int DateCurr = mTransactions.get(i).getCalender().get(Calendar.DAY_OF_MONTH);
                    if (DateCurr == mTransactions.get(i-1).getCalender().get(Calendar.DAY_OF_MONTH)){
                        mObjects.add(mTransactions.get(i));
                    } else {
                        addSeparatorItem(mTransactions.get(i).getDate());
                        mObjects.add(mTransactions.get(i));
                    }
                }
            }
        }


        @SuppressWarnings("unchecked")
        private void addSeparatorItem(String label) {
            mObjects.add(label);
            mSeparatorPos.add(mObjects.size() - 1);
        }

        @Override
        public int getCount() {
            return mTransactions.size();
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return TYPE_MAX_COUNT;
        }

        @Override
        public Object getItem(int position) {
            return mObjects.get(position);
        }

        @Override
        public boolean isEnabled(int position) {
            if (getItemViewType(position) == TYPE_SEPARATOR) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            ViewHolder holder = null;
            int itemType = getItemViewType(position);
            if (convertView == null) {
                switch (itemType) {
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(
                                R.layout.listview_home_separator, null);
                        holder = new ViewHolder();
                        holder.tvDate = (TextView) convertView
                                .findViewById(R.id.tvSeparatorHomeGTime);
                        holder.tvAmount = (TextView) convertView
                                .findViewById(R.id.tvSeparatorTotalExpenses);
                        holder.tvSeparatorTotIncome = (TextView) convertView
                                .findViewById(R.id.tvSeparatorTotalIncome);
                        break;
                    case TYPE_TRANSACTION:
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
                        break;
                }
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            //Binding data to the convertview
            switch (itemType){
                case TYPE_SEPARATOR:
                    String labelDate;
                    labelDate = (String) mObjects.get(position);
                    holder.tvDate.setText(labelDate);
                    break;
                case TYPE_TRANSACTION:
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
                    break;
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
