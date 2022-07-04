import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IOrder, Order } from '../order.model';
import { OrderService } from '../service/order.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IBasket } from 'app/entities/basket/basket.model';
import { BasketService } from 'app/entities/basket/service/basket.service';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

@Component({
  selector: 'jhi-order-update',
  templateUrl: './order-update.component.html',
})
export class OrderUpdateComponent implements OnInit {
  isSaving = false;
  orderStatusValues = Object.keys(OrderStatus);

  usersSharedCollection: IUser[] = [];
  basketsSharedCollection: IBasket[] = [];

  editForm = this.fb.group({
    id: [],
    createDate: [null, [Validators.required]],
    status: [null, [Validators.required]],
    user: [],
    basket: [],
  });

  constructor(
    protected orderService: OrderService,
    protected userService: UserService,
    protected basketService: BasketService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ order }) => {
      this.updateForm(order);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const order = this.createFromForm();
    if (order.id !== undefined) {
      this.subscribeToSaveResponse(this.orderService.update(order));
    } else {
      this.subscribeToSaveResponse(this.orderService.create(order));
    }
  }

  trackUserById(_index: number, item: IUser): number {
    return item.id!;
  }

  trackBasketById(_index: number, item: IBasket): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrder>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(order: IOrder): void {
    this.editForm.patchValue({
      id: order.id,
      createDate: order.createDate,
      status: order.status,
      user: order.user,
      basket: order.basket,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, order.user);
    this.basketsSharedCollection = this.basketService.addBasketToCollectionIfMissing(this.basketsSharedCollection, order.basket);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.basketService
      .query()
      .pipe(map((res: HttpResponse<IBasket[]>) => res.body ?? []))
      .pipe(map((baskets: IBasket[]) => this.basketService.addBasketToCollectionIfMissing(baskets, this.editForm.get('basket')!.value)))
      .subscribe((baskets: IBasket[]) => (this.basketsSharedCollection = baskets));
  }

  protected createFromForm(): IOrder {
    return {
      ...new Order(),
      id: this.editForm.get(['id'])!.value,
      createDate: this.editForm.get(['createDate'])!.value,
      status: this.editForm.get(['status'])!.value,
      user: this.editForm.get(['user'])!.value,
      basket: this.editForm.get(['basket'])!.value,
    };
  }
}
