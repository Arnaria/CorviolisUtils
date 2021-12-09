package corviolis.corviolisutils.util;

import corviolis.corviolisutils.services.api.airtable.Rule;

import java.util.Comparator;

public class IdSorter implements Comparator<Rule>
{
    @Override
    public int compare(Rule o1, Rule o2) {
        return o1.getId().compareToIgnoreCase(o2.getId());
    }
}