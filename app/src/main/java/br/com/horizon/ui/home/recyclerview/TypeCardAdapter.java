package br.com.horizon.ui.home.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.horizon.databinding.TypeItemFragmentBinding;
import br.com.horizon.model.Security;

public class TypeCardAdapter extends RecyclerView.Adapter<TypeCardAdapter.ViewHolder> {
    private final TypeCardAdapter.OnItemClickListener onItemClickListener;
    private final List<String> types = new ArrayList<>();
    private final Context context;
    private TypeItemFragmentBinding binder;

    public TypeCardAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binder = TypeItemFragmentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        binder.setLifecycleOwner(holder);
        String securityType = types.get(position);
        holder.link(securityType);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.markAsActive();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.markAsDestroyed();
    }

    @Override
    public long getItemId(int position) {
        return types.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, LifecycleOwner {
        private final LifecycleRegistry registry = new LifecycleRegistry(this);
        private String securityType;

        ViewHolder(TypeItemFragmentBinding binder) {
            super(binder.getRoot());
            registry.setCurrentState(Lifecycle.State.INITIALIZED);
            binder.setItemClick(this);
        }

        private void markAsActive() {
            registry.setCurrentState(Lifecycle.State.STARTED);
        }

        private void markAsDestroyed() {
            registry.setCurrentState(Lifecycle.State.DESTROYED);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(securityType);
        }

        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            return registry;
        }

        void link(String securityType) {
            this.securityType = securityType;
            binder.setType(securityType);
        }
    }

    public void addAll(List<String> securities) {
        types.clear();
        types.addAll(securities);
    }

    public interface OnItemClickListener {
        void onItemClick(String securityType);
    }
}
