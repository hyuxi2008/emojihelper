package com.liang.furniture.ui.shopcart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import com.doug.emojihelper.R;
import com.liang.AppConstants;
import com.liang.furniture.adapter.CommonAdapter;
import com.liang.furniture.adapter.ProductUserAdapter;
import com.liang.furniture.adapter.ProductUserAdapter.ItemCallBack;
import com.liang.furniture.adapter.ViewHolder;
import com.liang.furniture.bean.jsonbean.Product;
import com.liang.furniture.cache.CacheBean;
import com.liang.furniture.support.UIHelper;
import com.liang.furniture.ui.product.ProductDetailActivity;
import com.liang.furniture.utils.FormatUtils;
import com.louding.frame.KJActivity;
import com.louding.frame.KJHttp;
import com.louding.frame.http.HttpCallBack;
import com.louding.frame.http.HttpParams;
import com.louding.frame.ui.BindView;
import com.louding.frame.widget.KJListView;
import com.louding.frame.widget.KJRefreshListener;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShopCartListActivity extends KJActivity {

	@BindView(id = R.id.container)
	private RelativeLayout container;

	@BindView(id = R.id.img_right)
	private ImageView imgRight;

	@BindView(id = R.id.listview)
	private KJListView listview;
	
	private Map<String, List<Product>> products;
	
	private Map<Integer, List<Product>> typeProducts;
	
	

	private int page = 1;

	private ProductUserAdapter adapter;

	@Override
	public void setRootView() {
		setContentView(R.layout.activity_shopcart_list);
		UIHelper.setTitleView(this, "", "商品列表");
//		UIHelper.setBtnRight(aty, R.drawable.icon_search_off, this);
	}


	@Override
	public void widgetClick(View v) {
		super.widgetClick(v);
		switch (v.getId()) {
			case R.id.flright :

				break;
		}
	}

	private void getData(final int pageIndex) {
		products = new HashMap<String, List<Product>>();
		typeProducts = CacheBean.getInstance().getProducts();
		for (List<Product> typeProduct : typeProducts.values()) {
			for (int i = 0; i < typeProduct.size(); i++) {
				String pid = typeProduct.get(i).getPid();
				if (!products.containsKey(pid)) {
					List<Product> current = new ArrayList<Product>();
					current.add(typeProduct.get(i));
					products.put(pid, current);
				}
				else {
					products.get(pid).add(typeProduct.get(i));
				}
			}
		}
	}

	@Override
	public void initWidget() {
		super.initWidget();
		adapter = new ProductUserAdapter(this);
		adapter.setItemCallBack(new ItemCallBack() {
			
			@Override
			public void onPlusClick(int position, Product item) {
				
			}

			@Override
			public void onMinusClick(int position, Product item) {
				List<Product> typeProducts = CacheBean.getInstance().getProducts().get(item.getType());
				if (null == typeProducts) {
					typeProducts = new ArrayList<Product>(); 
				}
				typeProducts.add(item);
				//重写product的equals
				CacheBean.getInstance().getProducts().get(item.getType()).remove(item);
			}
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				getProductDetail(adapter.getDatas().get(position));
			}
			
		});
		listview.setAdapter(adapter);
		listview.setOnRefreshListener(refreshListener);
		listview.setEmptyView(findViewById(R.id.empty));
		getData(1);

	}
	
	/**
	 * 获取用户详情
	 * @param dto
	 */
	private void getProductDetail(Product dto) {
		HttpParams params = new HttpParams(dto);
		KJHttp kjh = new KJHttp();
		kjh.post(AppConstants.PRODUCT_LIST, params, new HttpCallBack(this) {
			
			@Override
			public void success(JSONObject ret) {
				super.success(ret);
				try {
					Product dto = FormatUtils.jsonParse(ret.toString(), Product.class);
					Intent i = new Intent(ShopCartListActivity.this, ProductDetailActivity.class);
					i.putExtra("product", dto);
					startActivity(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
	}

	private boolean noMoreData = false;

	private KJRefreshListener refreshListener = new KJRefreshListener() {
		@Override
		public void onRefresh() {
			getData(1);
		}

		@Override
		public void onLoadMore() {
			if (!noMoreData) {
				getData(page + 1);
			}
		}
	};

}
