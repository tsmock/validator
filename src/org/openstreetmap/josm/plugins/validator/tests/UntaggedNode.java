package org.openstreetmap.josm.plugins.validator.tests;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.*;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.validator.Severity;
import org.openstreetmap.josm.plugins.validator.Test;
import org.openstreetmap.josm.plugins.validator.TestError;
/**
 * Checks for untagged nodes that are in no way
 * 
 * @author frsantos
 */
public class UntaggedNode extends Test 
{
    /** Tags allowed in a node */
    public static String[] allowedTags = new String[] { "created_by" };
    
    /** Bag of all nodes */
    Set<Node> emptyNodes;
    
    /**
     * Constructor
     */
    public UntaggedNode() 
    {
        super(tr("Untagged nodes."),
              tr("This test checks for untagged nodes that are not part of any way."));
    }

    @Override
    public void startTest() 
    {
        emptyNodes = new HashSet<Node>(100);
    }
    
    @Override
    public void visit(Collection<OsmPrimitive> selection) 
    {
        // If there is a partial selection, it may be false positives if a
        // node is selected, but not the container way. So, in this
        // case, we must visit all ways, selected or not.
        if (partialSelection) {
            for (OsmPrimitive p : selection) {
                if (!p.deleted && p instanceof Node) {
                    p.visit(this);
                }
            }
            for (Way w : Main.ds.ways) {
                visit(w);
            }
        } else {
            for (OsmPrimitive p : selection) {
                if (!p.deleted) {
                    p.visit(this);
                }
            }
        }
    }
    
    @Override
    public void visit(Node n) 
    {
        int numTags = 0;
        Map<String, String> tags = n.keys;
        if( tags != null )
        {
            numTags = tags.size();
            for( String tag : allowedTags)
                if( tags.containsKey(tag) ) numTags--;
        }
        
        if( numTags == 0 )
        {
            emptyNodes.add(n);
        }
    }
    
    @Override
    public void visit(Way w) 
    {
        for (Node n : w.nodes) {
            emptyNodes.remove(n);
        }
    }
    
    @Override
    public void endTest() 
    {
        for(Node node : emptyNodes)
        {
            errors.add( new TestError(this, Severity.OTHER, tr("Untagged and unconnected nodes"), node) );
        }
        emptyNodes = null;
    }
    
    @Override
    public Command fixError(TestError testError)
    {
        return new DeleteCommand(testError.getPrimitives());
    }
    
    @Override
    public boolean isFixable(TestError testError)
    {
        return (testError.getTester() instanceof UntaggedNode);
    }		
}
