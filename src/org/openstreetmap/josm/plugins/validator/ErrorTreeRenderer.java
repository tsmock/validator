package org.openstreetmap.josm.plugins.validator;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.openstreetmap.josm.plugins.validator.util.MultipleNameVisitor;
import org.openstreetmap.josm.tools.ImageProvider;

/**
 * Tree renderer for displaying errors
 * @author frsantos
 */
public class ErrorTreeRenderer extends DefaultTreeCellRenderer
{
    /** Serializable ID */
    private static final long serialVersionUID = 5567632718124640198L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) 
    {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        Object nodeInfo = node.getUserObject();

        if (nodeInfo instanceof Severity)
        {
            Severity s = (Severity)nodeInfo;
            setIcon(ImageProvider.get("data", s.getIcon()));
        }
        else if (nodeInfo instanceof TestError)
        {
            TestError error = (TestError)nodeInfo;
            MultipleNameVisitor v = new MultipleNameVisitor();
            v.visit(error.getPrimitives());
            setText(v.getText());
            setIcon(v.getIcon());
        }

        return this;
    }
}
