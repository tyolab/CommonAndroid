package au.com.tyo.android.adapter;

public class SectionTitleItem implements ListItemViewType {

    private String title;

    public SectionTitleItem(String title) {
        this.title = title;
    }

    @Override
    public int getViewType() {
        return ListWithHeadersAdapter.ITEM_TYPE_SECTION_HEADER;
    }

    @Override
    public String toString() {
        return title;
    }
}
