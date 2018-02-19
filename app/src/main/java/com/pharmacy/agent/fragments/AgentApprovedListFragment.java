package com.pharmacy.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pharmacy.R;

/**
 * Created by PCCS-0007 on 06-Feb-18.
 */

public class AgentApprovedListFragment  extends Fragment{

    public AgentApprovedListFragment()
    {

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agent_approved_list, container, false);
    }
}
