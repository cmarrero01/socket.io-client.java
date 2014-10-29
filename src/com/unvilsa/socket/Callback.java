package com.unvilsa.socket;

import org.json.JSONObject;

public interface Callback {
	void OnFinished(JSONObject data);
	void OnError(Object... args);
}
