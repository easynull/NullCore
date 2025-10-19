package com.mw.nullcore.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class SearchBox extends EditBox {
    private List<String> searchList = new ArrayList<>();
    private Map<String, Object> searchMap = new HashMap<>();
    private Consumer<List<String>> consumerResult;
    private final List<String> result = new ArrayList<>();
    private boolean searchInMapKeys;

    public SearchBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.setResponder(text -> search());
    }

    public SearchBox searchList(List<String> items) {
        this.searchList = new ArrayList<>(items);
        this.searchInMapKeys = false;
        return this;
    }

    public SearchBox searchMap(Map<String, Object> map, boolean searchKeys) {
        this.searchMap = new HashMap<>(map);
        this.searchInMapKeys = searchKeys;
        this.searchList = searchKeys ? new ArrayList<>(map.keySet()) : new ArrayList<>();
        return this;
    }

    public void searchResult(Consumer<List<String>> consumer) {
        this.consumerResult = consumer;
    }

    public List<String> getResult(){
        return result;
    }

    private void search() {
        String searchText = getValue().toLowerCase();
        List<String> results = new ArrayList<>();
        if (!searchMap.isEmpty()) {
            searchMap.forEach((key, value) -> {
                if (searchInMapKeys && key.toLowerCase().contains(searchText)) {
                    results.add(key);
                } else if (!searchInMapKeys && value.toString().toLowerCase().contains(searchText)) {
                    results.add(value.toString());
                }
            });
        } else {
            searchList.stream().filter(obj -> obj.toLowerCase().contains(searchText)).forEach(results::add);
        }
        if (consumerResult != null) {
            consumerResult.accept(results);
        }
        result.addAll(results);
    }
}