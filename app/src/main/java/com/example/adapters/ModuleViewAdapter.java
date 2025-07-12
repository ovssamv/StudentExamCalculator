package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calculclau.R;
import com.example.models.Module;

import java.util.List;

public class ModuleViewAdapter extends RecyclerView.Adapter<ModuleViewAdapter.ModuleViewHolder> {

    private final List<Module> moduleList;

    public ModuleViewAdapter(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        holder.moduleName.setText(module.getName());
        holder.moduleCoef.setText("Coefficient: " + module.getCoefficient());
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    public static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView moduleName;
        TextView moduleCoef;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleName = itemView.findViewById(R.id.simple_module_name);
            moduleCoef = itemView.findViewById(R.id.simple_module_coefficient);
        }
    }
}
