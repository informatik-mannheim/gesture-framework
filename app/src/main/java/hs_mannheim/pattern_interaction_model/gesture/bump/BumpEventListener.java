package hs_mannheim.pattern_interaction_model.gesture.bump;

import java.util.List;

public interface BumpEventListener {
    void onBump(List<Sample> samples);
}
