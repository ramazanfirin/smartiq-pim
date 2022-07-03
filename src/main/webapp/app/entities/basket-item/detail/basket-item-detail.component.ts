import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBasketItem } from '../basket-item.model';

@Component({
  selector: 'jhi-basket-item-detail',
  templateUrl: './basket-item-detail.component.html',
})
export class BasketItemDetailComponent implements OnInit {
  basketItem: IBasketItem | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ basketItem }) => {
      this.basketItem = basketItem;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
