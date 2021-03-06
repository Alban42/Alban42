package com.alma42.mapgen.utils.voronoi;

import java.util.ArrayList;

import com.alma42.mapgen.utils.geometry.Point;

public final class HalfedgePriorityQueue // also known as heap
{

  private ArrayList<Halfedge> _hash;
  private int                 _count;
  private int                 _minBucket;
  private int                 _hashsize;
  private double              _ymin;
  private double              _deltay;

  public HalfedgePriorityQueue(double ymin, double deltay, int sqrt_nsites) {
    this._ymin = ymin;
    this._deltay = deltay;
    this._hashsize = 4 * sqrt_nsites;
    initialize();
  }

  public void dispose() {
    // get rid of dummies
    for (int i = 0; i < this._hashsize; ++i) {
      this._hash.get(i).dispose();
    }
    this._hash.clear();
    this._hash = null;
  }

  private void initialize() {
    int i;

    this._count = 0;
    this._minBucket = 0;
    this._hash = new ArrayList<Halfedge>(this._hashsize);
    // dummy Halfedge at the top of each hash
    for (i = 0; i < this._hashsize; ++i) {
      this._hash.add(Halfedge.createDummy());
      this._hash.get(i).nextInPriorityQueue = null;
    }
  }

  public void insert(Halfedge halfEdge) {
    Halfedge previous, next;
    int insertionBucket = bucket(halfEdge);
    if (insertionBucket < this._minBucket) {
      this._minBucket = insertionBucket;
    }
    previous = this._hash.get(insertionBucket);
    while ((next = previous.nextInPriorityQueue) != null
        && (halfEdge.ystar > next.ystar || (halfEdge.ystar == next.ystar && halfEdge.vertex.get_x() > next.vertex
            .get_x()))) {
      previous = next;
    }
    halfEdge.nextInPriorityQueue = previous.nextInPriorityQueue;
    previous.nextInPriorityQueue = halfEdge;
    ++this._count;
  }

  public void remove(Halfedge halfEdge) {
    Halfedge previous;
    int removalBucket = bucket(halfEdge);

    if (halfEdge.vertex != null) {
      previous = this._hash.get(removalBucket);
      while (previous.nextInPriorityQueue != halfEdge) {
        previous = previous.nextInPriorityQueue;
      }
      previous.nextInPriorityQueue = halfEdge.nextInPriorityQueue;
      this._count--;
      halfEdge.vertex = null;
      halfEdge.nextInPriorityQueue = null;
      halfEdge.dispose();
    }
  }

  private int bucket(Halfedge halfEdge) {
    int theBucket = (int) ((halfEdge.ystar - this._ymin) / this._deltay * this._hashsize);
    if (theBucket < 0) {
      theBucket = 0;
    }
    if (theBucket >= this._hashsize) {
      theBucket = this._hashsize - 1;
    }
    return theBucket;
  }

  private boolean isEmpty(int bucket) {
    return (this._hash.get(bucket).nextInPriorityQueue == null);
  }

  /**
   * move _minBucket until it contains an actual Halfedge (not just the dummy
   * at the top);
   * 
   */
  private void adjustMinBucket() {
    while (this._minBucket < this._hashsize - 1 && isEmpty(this._minBucket)) {
      ++this._minBucket;
    }
  }

  public boolean empty() {
    return this._count == 0;
  }

  /**
   * @return coordinates of the Halfedge's vertex in V*, the transformed
   *         Voronoi diagram
   * 
   */
  public Point min() {
    adjustMinBucket();
    Halfedge answer = this._hash.get(this._minBucket).nextInPriorityQueue;
    return new Point(answer.vertex.get_x(), answer.ystar);
  }

  /**
   * remove and return the min Halfedge
   * 
   * @return
   * 
   */
  public Halfedge extractMin() {
    Halfedge answer;

    // get the first real Halfedge in _minBucket
    answer = this._hash.get(this._minBucket).nextInPriorityQueue;

    this._hash.get(this._minBucket).nextInPriorityQueue = answer.nextInPriorityQueue;
    this._count--;
    answer.nextInPriorityQueue = null;

    return answer;
  }
}